package ru.mrkotyaka.commonlibs.dto.order;

import lombok.Builder;

import java.util.Set;

@Builder
public record PriceRequestDto(
        String storeName,
        Set<OrderItemRqDto> items) {
}
