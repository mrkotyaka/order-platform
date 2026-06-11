package ru.mrkotyaka.commonlibs.kafka.notification;

import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;

import java.util.UUID;

public record UserNotificationDto(
        UUID id,
        String email,
        String phone,
        String pushToken,
        NotificationPreference notificationPreference
) {
}
