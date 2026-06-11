package ru.mrkotyaka.commonlibs.kafka.notification;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationType;

import java.util.UUID;

@Builder
public record NotificationEvent(
        UUID userId,
        NotificationType type,
        String message
) {
}
