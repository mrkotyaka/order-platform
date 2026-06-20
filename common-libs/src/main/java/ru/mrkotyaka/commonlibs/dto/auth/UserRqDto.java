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
    public UserRqDto(String login, String password, String name, String address, String email, String phone, UserRoles role, NotificationPreference notificationPreference) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.notificationPreference = notificationPreference;
    }

    public UserRqDto(String login, String password) {
        this(login, password, "", "", "", "", null, null);
    }
}
