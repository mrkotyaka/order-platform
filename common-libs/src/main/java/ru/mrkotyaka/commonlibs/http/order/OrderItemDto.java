package ru.mrkotyaka.commonlibs.http.order;

import java.math.BigDecimal;

public record OrderItemDto(
//        Long id,
//        Long itemId,
        String name,
        Integer quantity,
        BigDecimal price
) {
}
