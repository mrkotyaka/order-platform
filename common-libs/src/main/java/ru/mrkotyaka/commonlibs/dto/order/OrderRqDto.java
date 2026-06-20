package ru.mrkotyaka.commonlibs.dto.order;

import java.util.Set;

public record OrderRqDto(
        String storeName,
        String deliveryAddress,
        Set<OrderItemRqDto> items,
        String comment
) {
}
