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
import ru.mrkotyaka.commonlibs.dto.order.PriceRequestDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationType;
import ru.mrkotyaka.commonlibs.enums.order.CashFlow;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentStatus;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;
import ru.mrkotyaka.orderservice.domain.db.OrderEntity;
import ru.mrkotyaka.orderservice.domain.db.OrderItemEntity;
import ru.mrkotyaka.orderservice.domain.db.OrderMapper;
import ru.mrkotyaka.orderservice.domain.db.OrderRepository;
import ru.mrkotyaka.orderservice.external.DeliveryHttpClient;
import ru.mrkotyaka.orderservice.external.PaymentHttpClient;
import ru.mrkotyaka.orderservice.external.StoreHttpClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final DeliveryHttpClient deliveryHttpClient;
    private final StoreHttpClient storeHttpClient;
    private final KafkaTemplate<UUID, OrderPaidEvent> kafkaTemplate;
    private final KafkaTemplate<UUID, NotificationEvent> notificationKafkaTemplate;
    private final KafkaTemplate<UUID, OrderRsDto> warehouseKafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    @Value("${notification-topic}")
    private String notificationTopic;

    @Value("${warehouse-topic}")
    private String warehouseTopic;

    @Transactional
    public OrderRsDto create(OrderRqDto request, UUID authUserId) {

        var priceRequestDto = PriceRequestDto.builder()
                .storeName(request.storeName())
                .items(request.items())
                .build();

        var orderItemRsDtos = storeHttpClient.getItemPrice(priceRequestDto);

        var orderEntity = orderMapper.toOrderEntity(request);
        orderEntity.setCustomerId(authUserId);
        orderEntity.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        var orderItemEntities = orderItemRsDtos.stream()
                .map(item -> {
                            OrderItemEntity orderItemEntity = orderMapper.toOrderItemEntity(item);
                            orderItemEntity.setOrderId(orderEntity);
                            return orderItemEntity;
                        }
                )
                .collect(Collectors.toSet());
        orderEntity.setItems(orderItemEntities);

        BigDecimal totalAmount = orderItemRsDtos.stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        orderEntity.setTotalAmount(totalAmount);
        var saved = orderRepository.save(orderEntity);

        sendNotification(
                saved.getCustomerId(),
                NotificationType.ORDER_CREATED,
                "Your order `%s` has been successfully created in the amount of %s ₽".formatted(
                        saved.getId(),
                        saved.getTotalAmount()
                )
        );

        log.info("Order `{}` was created successfully", saved.getId());
        return orderMapper.toOrderRsDto(saved);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderById(UUID orderId) {
        var order = orderRepository.findOrderById(orderId);
        return order.orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Order `%s` not found".formatted(orderId)));
    }

    @Transactional(readOnly = true)
    public List<OrderRsDto> getAllOrders() {
        List<OrderRsDto> allOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAll();

        for (var order : allOrders) {
            allOrdersDTO.add(orderMapper.toOrderRsDto(order));
        }

        return allOrdersDTO;
    }

    @Transactional(readOnly = true)
    public List<OrderRsDto> getAllPendingPaymentOrders() {
        List<OrderRsDto> allPendingPaymentOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAllPendingPayment();

        for (var order : allOrders) {
            allPendingPaymentOrdersDTO.add(orderMapper.toOrderRsDto(order));
        }

        return allPendingPaymentOrdersDTO;
    }

    @Transactional
    public OrderRsDto processPayment(
            UUID orderId,
            OrderPaymentRqDto request
    ) {
        var entity = getOrderById(orderId);

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
        return orderMapper.toOrderRsDto(saved);
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
        var order = getOrderById(event.orderId());

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
        return orderMapper.toOrderRsDto(saved);
    }

    public void sendNotification(UUID userId, NotificationType notificationType, String message) {
        notificationKafkaTemplate.send(
                notificationTopic,
                userId,
                new NotificationEvent(userId, notificationType, message)
        ).thenAccept(result ->
                log.info("Notification event sent: userId={}, type={}", userId, notificationType));
    }

    @Transactional
    public OrderRsDto cancelOrder(UUID orderId) {
        var entity = getOrderById(orderId);
        var actualStatus = entity.getOrderStatus();
        log.info("Processor: ActualStatus={}", actualStatus);

        if (actualStatus.equals(OrderStatus.DELIVERY_ASSIGNED) && !isPossibleCancel(orderId)) {
            log.info("Processor: You can not cancel order `{}`. Delivery already process", orderId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can not cancel order `%s`. Delivery already process".formatted(orderId));
        }

        return switch (actualStatus) {
            case PAYMENT_FAILED ->
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Processor: Order status Payment failed");
            case CANCELED -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Processor: Order status Canceled");
            case DELIVERED -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Processor: Please call our office");
            case PENDING_PAYMENT -> {
                entity.setOrderStatus(OrderStatus.CANCELED);
                var saved = orderRepository.save(entity);

                warehouseRefundedEventSending(saved);

                log.info("Processor: Order `{}` was cancelled", orderId);
                yield orderMapper.toOrderRsDto(saved);
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

                warehouseRefundedEventSending(saved);

                if (actualStatus.equals(OrderStatus.DELIVERY_ASSIGNED)) {
                    sendNotification(
                            deliveryHttpClient.getCourierId(orderId),
                            NotificationType.DELIVERY_CANCELLED,
                            "Dear courier! Delivery of the order `%s` was canceled".formatted(saved.getId())
                    );
                }
                yield orderMapper.toOrderRsDto(saved);
            }
        };
    }

    private void warehouseRefundedEventSending(OrderEntity saved) {
        log.info("Processor: Sending event for refunded stocks");
        warehouseKafkaTemplate.send(
                warehouseTopic,
                saved.getId(),
                orderMapper.toOrderRsDto(saved)
        ).thenAccept(result -> log.info("Processor: Order `{}` warehouse event sent", saved.getId()));
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
            ordersDto.add(orderMapper.toOrderRsDto(entity));
        }
        return ordersDto;
    }

    @Transactional(readOnly = true)
    public boolean canForReview(UUID userId, UUID orderId) {
        log.info("existsByOrderIdAndCustomerId: userId={}, orderId={}", userId, orderId);
        return orderRepository.existsByOrderIdAndCustomerId(userId, orderId);
    }
}

