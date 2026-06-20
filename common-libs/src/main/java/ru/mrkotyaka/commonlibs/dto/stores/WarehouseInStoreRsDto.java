package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record WarehouseInStoreRsDto(
        UUID productId,
        String productName,
        BigDecimal price,
        BigDecimal stock
) {
}
