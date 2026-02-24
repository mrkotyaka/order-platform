package ru.mrkotyaka.orderservice.api;

import jakarta.persistence.Column;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity,
        String name
) {
}
