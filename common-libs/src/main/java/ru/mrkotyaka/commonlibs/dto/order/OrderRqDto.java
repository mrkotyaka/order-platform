package ru.mrkotyaka.commonlibs.dto.order;

import java.util.Set;

public record OrderRqDto(
        String address,
        Set<OrderItemRqDto> items,
        String comment
) {
}
