package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record StoreProductInProductRsDto(
        UUID storeId,
        String storeName,
        BigDecimal price,
        BigDecimal stock
) {
}
