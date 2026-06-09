package ru.mrkotyaka.commonlibs.http.order;

import java.util.Set;

public record CreateOrderRqDto(
        String address,
        Set<OrderItemRqDto> items
) {
}
