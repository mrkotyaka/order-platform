package ru.mrkotyaka.commonlibs.http.delivery;

public record CourierDTO(
        Long id,
        String name,
        Double rating,
        String address,
        String email,
        String phone
) {}
