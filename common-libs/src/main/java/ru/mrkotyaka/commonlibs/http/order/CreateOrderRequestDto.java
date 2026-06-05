package ru.mrkotyaka.commonlibs.http.order;

import java.util.Set;

public record CreateOrderRequestDto(
        String address,
        Set<OrderItemRequestDto> items
) {
}
