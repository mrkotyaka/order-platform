package ru.mrkotyaka.commonlibs.http.user;

public record UserResponse(
        String username,
        String email,
        String phone
) {
}
