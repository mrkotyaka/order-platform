package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record WarehouseInProductRsDto(
        UUID storeId,
        String storeName,
        BigDecimal price,
        BigDecimal stock
) {
}
