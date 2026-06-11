package ru.mrkotyaka.commonlibs.dto.order;

public record OrderItemRqDto(
        Integer quantity,
        String name
) {
}
