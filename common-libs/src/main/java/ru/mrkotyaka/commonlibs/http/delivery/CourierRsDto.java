package ru.mrkotyaka.commonlibs.http.delivery;

public record CourierRsDto(
        Long id,
        String name,
        Double rating,
        String address,
        String email,
        String phone
) {}
