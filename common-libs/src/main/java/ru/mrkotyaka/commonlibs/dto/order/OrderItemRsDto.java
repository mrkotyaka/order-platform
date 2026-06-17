package ru.mrkotyaka.commonlibs.dto.order;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemRsDto(
        String name,
        Integer quantity,
        BigDecimal price
) {
}
