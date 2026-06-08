package ru.mrkotyaka.commonlibs.http.auth;

public record CustomerResponseDTO(
        String username,
        String email,
        String phone,
        String roles
) {
}
