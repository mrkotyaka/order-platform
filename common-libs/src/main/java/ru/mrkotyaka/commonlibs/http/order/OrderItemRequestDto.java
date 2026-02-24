package ru.mrkotyaka.commonlibs.http.order;

public record OrderItemRequestDto(
        Long itemId,
        Integer quantity,
        String name
) {
}
