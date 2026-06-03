package ru.mrkotyaka.commonlibs.http.auth;

public record AuthRequest(
        String username,
        String password) {
}
