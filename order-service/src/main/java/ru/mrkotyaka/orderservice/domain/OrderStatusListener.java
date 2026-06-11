package ru.mrkotyaka.orderservice.domain;

import jakarta.persistence.PreUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationType;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;
import ru.mrkotyaka.orderservice.domain.db.OrderEntity;

@Component
public class OrderStatusListener {
    private static OrderProcessor orderProcessor;

    @Autowired
    public void setOrderProcessor(OrderProcessor processor) {
        OrderStatusListener.orderProcessor = processor;
    }

    @PreUpdate
    public void onStatusChange(OrderEntity order) {
        if (orderProcessor == null) return;

        String message = switch (order.getOrderStatus()) {
            case PAID -> "Payment for the order `%s` was successful".formatted(order.getId());
            case PAYMENT_FAILED -> "Payment for the order `%s` did not go through. Try again".formatted(order.getId());
            case DELIVERY_ASSIGNED ->
                    "Courier %s assigned to order `%s`. Expect in %d minutes".formatted(order.getCourierName(), order.getId(), order.getEtaMinutes());
            default -> null;
        };

        if (message != null) {
            orderProcessor.sendNotification(
                    order.getCustomerId(),
                    toNotificationType(order.getOrderStatus()),
                    message
            );
        }
    }

    private NotificationType toNotificationType(OrderStatus status) {
        return switch (status) {
            case PAID -> NotificationType.PAYMENT_SUCCESS;
            case PAYMENT_FAILED -> NotificationType.PAYMENT_FAILED;
            case DELIVERY_ASSIGNED -> NotificationType.COURIER_ASSIGNED;
            default -> null;
        };
    }
}
