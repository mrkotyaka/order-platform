package ru.mrkotyaka.commonlibs.http.order;

public record OrderItemRqDto(
        Integer quantity,
        String name
) {
}
