package ru.mrkotyaka.orderservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderPaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationType;
import ru.mrkotyaka.commonlibs.enums.order.CashFlow;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentStatus;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;
import ru.mrkotyaka.orderservice.domain.db.*;
import ru.mrkotyaka.orderservice.external.DeliveryHttpClient;
import ru.mrkotyaka.orderservice.external.PaymentHttpClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ItemRepository itemRepository;
    private final PaymentHttpClient paymentHttpClient;
    private final DeliveryHttpClient deliveryHttpClient;
    private final KafkaTemplate<UUID, OrderPaidEvent> kafkaTemplate;
    private final KafkaTemplate<UUID, NotificationEvent> notificationKafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    @Value("${notification-topic}")
    private String notificationTopic;

    @Transactional
    public OrderRsDto create(OrderRqDto request, UUID authUserId) {
        var entity = orderMapper.toOrderEntity(request);
        entity.setCustomerId(authUserId);
        calcPricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        var saved = orderRepository.save(entity);

        sendNotification(
                saved.getCustomerId(),
                NotificationType.ORDER_CREATED,
                "Your order `%s` has been successfully created in the amount of %s ₽".formatted(
                        saved.getId(),
                        saved.getTotalAmount()
                )
        );
        log.info("Order `{}` was created successfully", saved.getId());
        return orderMapper.toOrderDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderOrThrow(UUID id) {
        var order = orderRepository.findWithItemsById(id);
        return order
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Order `%s` not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public List<OrderRsDto> getAllOrders() {
        List<OrderRsDto> allOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAll();
        for (var order : allOrders) {
            allOrdersDTO.add(orderMapper.toOrderDto(order));
        }
        return allOrdersDTO;
    }

    @Transactional(readOnly = true)
    public List<OrderRsDto> getAllPendingPaymentOrders() {
        List<OrderRsDto> allPendingPaymentOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAllPendingPayment();
        for (var order : allOrders) {
            allPendingPaymentOrdersDTO.add(orderMapper.toOrderDto(order));
        }
        return allPendingPaymentOrdersDTO;
    }


    private void calcPricingForOrder(OrderEntity orderEntity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item : orderEntity.getItems()) {
            if (!itemRepository.existsByName(item.getName())) {
                log.info("Item `{}` not found. Please use items list", item.getName());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                var itemPrice = itemRepository.findPriceByName(item.getName());
                item.setPrice(BigDecimal.valueOf(itemPrice));
                totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).add(totalPrice);
            }
        }
        log.info("The calculation is over");
        orderEntity.setTotalAmount(totalPrice);
    }

    @Transactional
    public OrderRsDto processPayment(
            UUID orderId,
            OrderPaymentRqDto request
    ) {
        var entity = getOrderOrThrow(orderId);
        if (!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new RuntimeException("Order status is not PENDING_PAYMENT");
        }
        var response = paymentHttpClient
                .doPayment(PaymentRqDto.builder()
                        .orderId(orderId)
                        .paymentMethod(request.paymentMethod())
                        .amount(entity.getTotalAmount())
                        .cashFlow(CashFlow.DEBIT)
                        .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;

        entity.setOrderStatus(status);

        if (status.equals(OrderStatus.PAID)) {
            sendOrderPaidEvent(entity, response, CashFlow.DEBIT);
        }
        var saved = orderRepository.save(entity);
        return orderMapper.toOrderDto(saved);
    }

    private void sendOrderPaidEvent(
            OrderEntity order,
            PaymentRsDto response,
            CashFlow cashFlow
    ) {
        kafkaTemplate.send(
                orderPaidTopic,
                order.getId(),
                OrderPaidEvent.builder()
                        .orderId(order.getId())
                        .amount(order.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .cashFlow(cashFlow)
                        .build()
        ).thenAccept(result -> log.info("Order `{}` Paid event sent", order.getId()));
    }

    @Transactional
    public void processDeliveryAssigned(DeliveryAssignedEvent event) {
        var order = getOrderOrThrow(event.orderId());

        if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
            processIncorrectDeliveryState(order);
            return;
        }

        log.info("Start delivery assigning");

        order.setOrderStatus(OrderStatus.DELIVERY_ASSIGNED);
        order.setCourierName(event.courierName());
        order.setEtaMinutes(event.etaMinutes());
        var saved = orderRepository.save(order);

        sendNotification(
                event.userId(),
                NotificationType.COURIER_ASSIGNED,
                "You have been assigned delivery of the order `%s`".formatted(saved.getId())
        );
        log.info("Order `{}` delivery assigned processed", order.getId());
    }

    private void processIncorrectDeliveryState(OrderEntity order) {
        if (order.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)) {
            log.info("Order `{}` delivery already assigned", order.getId());
        } else {
            log.error("Trying to assign delivery but order have incorrect state: `{}`", order.getId());
        }
    }

    @Transactional
    public OrderRsDto processDeliveredState(UUID orderId) {
        var entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order `%s` not found but it is impossible".formatted(orderId)));

        if (!entity.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)) {
            log.error("You are trying to specify the Delivered status for order `{}` with an incorrect status. Order status must be DELIVERY_ASSIGNED", entity.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is not `%s`".formatted(OrderStatus.DELIVERY_ASSIGNED));
        }

        entity.setOrderStatus(OrderStatus.DELIVERED);
        entity.setDeliveredAt(LocalDateTime.now());
        var saved = orderRepository.save(entity);
        log.info("Order `{}` delivered", orderId);
        return orderMapper.toOrderDto(saved);
    }

    public void sendNotification(UUID userId, NotificationType notificationType, String message) {
        notificationKafkaTemplate.send(
                notificationTopic,
                userId,
                new NotificationEvent(userId, notificationType, message)
        ).thenAccept(result ->
                log.info("Notification event sent: userId={}, type={}", userId, notificationType)
        );
    }

    @Transactional
    public OrderRsDto cancelOrder(UUID orderId) {
        var entity = getOrderOrThrow(orderId);
        var actualStatus = entity.getOrderStatus();

        if(actualStatus.equals(OrderStatus.DELIVERY_ASSIGNED) && !isPossibleCancel(orderId)) {
            log.info("You can not cancel order `{}`. Delivery already process",orderId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not cancel order `%s`. Delivery already process".formatted(orderId));
        }

        return switch (actualStatus) {
            case PAYMENT_FAILED ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status Payment failed");
            case CANCELED -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status Canceled");
            case DELIVERED -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please call our office");
            case PENDING_PAYMENT -> {
                entity.setOrderStatus(OrderStatus.CANCELED);
                var saved = orderRepository.save(entity);
                log.info("Order `{}` was cancelled", orderId);
                yield orderMapper.toOrderDto(saved);
            }
            case PAID, DELIVERY_ASSIGNED -> {
                var response = paymentHttpClient
                        .doPayment(PaymentRqDto.builder()
                                .orderId(orderId)
                                .amount(entity.getTotalAmount())
                                .cashFlow(CashFlow.CREDIT)
                                .build());

                var status = response.paymentStatus().equals(PaymentStatus.REFUNDED)
                        ? OrderStatus.CANCELED
                        : OrderStatus.PAYMENT_FAILED;
                entity.setOrderStatus(status);

                if (status.equals(OrderStatus.CANCELED)) {
                    sendOrderPaidEvent(entity, response, CashFlow.CREDIT);
                }

                var saved = orderRepository.save(entity);

                if(actualStatus.equals(OrderStatus.DELIVERY_ASSIGNED)) {
                    sendNotification(
                            deliveryHttpClient.getCourierId(orderId),
                            NotificationType.DELIVERY_CANCELLED,
                            "Dear courier! Delivery of the order `%s` was canceled".formatted(saved.getId())
                    );
                }
                yield orderMapper.toOrderDto(saved);
            }
        };
    }

    @Transactional(readOnly = true)
    public boolean isPossibleCancel(UUID orderId) {
        var entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order `%s` not find".formatted(orderId)));

        int randomMinutes = ThreadLocalRandom.current().nextInt(10, 20); // order picking simulation
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(randomMinutes);

        return !entity.getCreatedAt().isBefore(threshold);
    }

    @Transactional(readOnly = true)
    public List<OrderRsDto> getOrderByStatus(OrderStatus orderStatus) {
        var ordersDto = new ArrayList<OrderRsDto>();
        var entities = orderRepository.findAllByOrderStatus(orderStatus);
        for (var entity : entities) {
            ordersDto.add(orderMapper.toOrderDto(entity));
        }
        return ordersDto;
    }

    @Transactional(readOnly = true)
    public boolean canForReview(UUID userId, UUID orderId) {
        return orderRepository.existsByOrderIdAndCustomerId(userId, orderId);
    }
}

