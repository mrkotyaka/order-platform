package ru.mrkotyaka.commonlibs.http.auth;

public record AuthRequestDTO(
        String username,
        String password,
        String email,
        String phone) {
}
