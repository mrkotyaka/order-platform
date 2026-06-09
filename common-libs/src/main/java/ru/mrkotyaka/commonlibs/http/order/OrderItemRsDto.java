package ru.mrkotyaka.commonlibs.http.order;

import java.math.BigDecimal;

public record OrderItemRsDto(
        String name,
        Integer quantity,
        BigDecimal price
) {
}
