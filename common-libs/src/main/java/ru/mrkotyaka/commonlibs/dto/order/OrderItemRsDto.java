package ru.mrkotyaka.commonlibs.dto.order;

import java.math.BigDecimal;

public record OrderItemRsDto(
        String name,
        Integer quantity,
        BigDecimal price
) {
}
