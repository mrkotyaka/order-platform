package ru.mrkotyaka.commonlibs.dto.auth;

import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;
import ru.mrkotyaka.commonlibs.enums.auth.UserRoles;

import java.util.UUID;

public record UserRsDto(
        UUID userId,
        String login,
        String name,
        String address,
        String email,
        String phone,
        UserRoles role,
        NotificationPreference notificationPreference
) {
}
