package ru.mrkotyaka.commonlibs.kafka.notification;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;

import java.util.UUID;

@Builder
public record UserNotificationDto(
        UUID id,
        String email,
        String phone,
        String pushToken,
        NotificationPreference notificationPreference
) {
}
