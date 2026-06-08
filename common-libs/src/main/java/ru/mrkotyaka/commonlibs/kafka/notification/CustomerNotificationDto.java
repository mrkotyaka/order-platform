package ru.mrkotyaka.commonlibs.kafka.notification;

import ru.mrkotyaka.commonlibs.http.notification.NotificationPreference;

public record CustomerNotificationDto(
        Long id,
        String email,
        String phone,
        String pushToken,
        NotificationPreference notificationPreference
) {
}
