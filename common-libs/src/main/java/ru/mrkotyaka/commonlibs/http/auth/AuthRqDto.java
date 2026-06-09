package ru.mrkotyaka.commonlibs.http.auth;

import ru.mrkotyaka.commonlibs.http.notification.NotificationPreference;

public record AuthRqDto(
        String username,
        String password,
        String address,
        String email,
        String phone,
        NotificationPreference notificationPreference) {
}
