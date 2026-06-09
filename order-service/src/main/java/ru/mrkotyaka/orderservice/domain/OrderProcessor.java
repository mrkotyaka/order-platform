package ru.mrkotyaka.orderservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.http.item.ItemDTO;
import ru.mrkotyaka.commonlibs.http.notification.NotificationType;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.commonlibs.http.order.OrderPaymentRequest;
import ru.mrkotyaka.commonlibs.http.order.OrderStatus;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRequestDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentResponseDto;
import ru.mrkotyaka.commonlibs.http.payment.PaymentStatus;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;
import ru.mrkotyaka.orderservice.domain.db.*;
import ru.mrkotyaka.orderservice.external.PaymentHttpClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderMapper;
    private final ItemRepository itemRepository;
    private final ItemEntityMapper itemMapper;
    private final PaymentHttpClient paymentHttpClient;
    private final KafkaTemplate<Long, OrderPaidEvent> kafkaTemplate;
    private final KafkaTemplate<Long, NotificationEvent> notificationKafkaTemplate;

    @Value("${order-paid-topic}")
    private String orderPaidTopic;

    @Value("${notification-topic}")
    private String notificationTopic;

    @Transactional
    public OrderEntity create(CreateOrderRequestDto request, Long authenticatedUserId) {
        var entity = orderMapper.toOrderEntity(request);
        entity.setCustomerId(authenticatedUserId);
        calcPricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        var saved = orderRepository.save(entity);

        sendNotification(
                saved.getCustomerId(),
                NotificationType.ORDER_CREATED,
                "Your order #%d has been successfully created in the amount of %s ₽".formatted(
                        saved.getId(),
                        saved.getTotalAmount()
                )
        );
        log.info("Order #{} was created successfully", saved.getId());
        return saved;
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrderOrThrow(Long id) {
        var orderItemEntityOpt = orderRepository.findWithItemsById(id);
        return orderItemEntityOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        List<OrderDto> allOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAll();
        for (var order : allOrders) {
            allOrdersDTO.add(orderMapper.toOrderDto(order));
        }
        log.info("{} orders was found", (long) allOrdersDTO.size());
        return allOrdersDTO;
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllPendingPaymentOrders() {
        List<OrderDto> allPendingPaymentOrdersDTO = new ArrayList<>();
        var allOrders = orderRepository.findAllPendingPayment();
        for (var order : allOrders) {
            allPendingPaymentOrdersDTO.add(orderMapper.toOrderDto(order));
        }
        log.info("{} pending payment orders was found", (long) allPendingPaymentOrdersDTO.size());
        return allPendingPaymentOrdersDTO;
    }


    private void calcPricingForOrder(OrderEntity orderEntity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemEntity item : orderEntity.getItems()) {
            if (!itemRepository.existsByName(item.getName())) {
                log.info("Item with name `{}` not found. Please use items list", item.getName());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                var itemPrice = itemRepository.findPriceByName(item.getName());
                item.setPrice(BigDecimal.valueOf(itemPrice));
                totalPrice = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).add(totalPrice);
            }
        }
        log.info("Calculated total price = {}", totalPrice);
        orderEntity.setTotalAmount(totalPrice);
    }

    @Transactional
    public OrderEntity processPayment(
            Long id,
            OrderPaymentRequest request
    ) {
        var entity = getOrderOrThrow(id);
        if (!entity.getOrderStatus().equals(OrderStatus.PENDING_PAYMENT)) {
            throw new RuntimeException("Order status is not PENDING_PAYMENT");
        }
        var response = paymentHttpClient
                .createPayment(CreatePaymentRequestDto.builder()
                        .orderId(id)
                        .paymentMethod(request.paymentMethod())
                        .amount(entity.getTotalAmount())
                        .build());

        var status = response.paymentStatus().equals(PaymentStatus.PAYMENT_SUCCEEDED)
                ? OrderStatus.PAID
                : OrderStatus.PAYMENT_FAILED;

        entity.setOrderStatus(status);

        if (status.equals(OrderStatus.PAID)) {
            log.info("Prepare to sending notification because status is {}", OrderStatus.PAID.name());
            sendOrderPaidEvent(entity, response);
        }
        return orderRepository.save(entity);
    }

    private void sendOrderPaidEvent(
            OrderEntity entity,
            CreatePaymentResponseDto response
    ) {
        kafkaTemplate.send(
                orderPaidTopic,
                entity.getId(),
                OrderPaidEvent.builder()
                        .orderId(entity.getId())
                        .amount(entity.getTotalAmount())
                        .paymentMethod(response.paymentMethod())
                        .paymentId(response.paymentId())
                        .build()
        ).thenAccept(result -> {
            log.info("Order Paid event sent: id={}", entity.getId());
        });
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
        orderRepository.save(order);
        log.info("Order delivery assigned processed: orderId={}", order.getId());
    }

    private void processIncorrectDeliveryState(OrderEntity order) {
        if (order.getOrderStatus().equals(OrderStatus.DELIVERY_ASSIGNED)) {
            log.info("Order delivery already assigned: orderId={}", order.getId());
        } else {
            log.error("Trying to assign delivery but order have incorrect state: state={}", order.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems() {
        List<ItemDTO> allItemDTO = new ArrayList<>();
        var allItems = itemRepository.findAll();
        for (var item : allItems) {
            allItemDTO.add(itemMapper.toItemDto(item));
        }
        return allItemDTO;
    }

    public void sendNotification(Long customerId, NotificationType notificationType, String payload) {
        notificationKafkaTemplate.send(
                notificationTopic,
                customerId,
                new NotificationEvent(customerId, notificationType, payload)
        ).thenAccept(result ->
                log.info("Notification event sent: customerId={}, type={}", customerId, notificationType)
        );
    }
}
