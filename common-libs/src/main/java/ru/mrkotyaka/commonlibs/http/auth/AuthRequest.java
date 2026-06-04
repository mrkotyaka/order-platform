package ru.mrkotyaka.commonlibs.http.auth;

public record AuthRequest(
        String username,
        String password,
        String admin_password) {
}
