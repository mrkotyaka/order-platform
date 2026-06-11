package ru.mrkotyaka.commonlibs.dto.auth;

import ru.mrkotyaka.commonlibs.enums.notification.NotificationPreference;
import ru.mrkotyaka.commonlibs.enums.auth.UserRoles;

public record UserRqDto(
        String login,
        String password,
        String name,
        String address,
        String email,
        String phone,
        UserRoles role,
        NotificationPreference notificationPreference
) {
}
