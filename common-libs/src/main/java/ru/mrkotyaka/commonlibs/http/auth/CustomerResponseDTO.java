package ru.mrkotyaka.commonlibs.http.auth;

import ru.mrkotyaka.commonlibs.http.notification.NotificationPreference;

public record CustomerResponseDTO(
        Long id,
        String username,
        String address,
        String email,
        String phone,
        String roles,
        NotificationPreference notificationPreference
) {
}
