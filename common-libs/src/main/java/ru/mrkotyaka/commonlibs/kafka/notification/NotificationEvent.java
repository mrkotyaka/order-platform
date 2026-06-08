package ru.mrkotyaka.commonlibs.kafka.notification;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.http.notification.NotificationType;

@Builder
public record NotificationEvent(
        Long customerId,
        NotificationType type,
        String payload
) {
}
