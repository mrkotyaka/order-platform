package ru.mrkotyaka.commonlibs.http.auth;

public record AuthRequest(
        String username,
        String password,
        String email,
        String phone,
        String admin_password) {
}
