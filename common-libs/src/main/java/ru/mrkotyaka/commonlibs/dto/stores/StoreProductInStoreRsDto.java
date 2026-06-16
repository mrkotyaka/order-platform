package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record StoreProductInStoreRsDto(
        UUID productId,
        String productName,
        BigDecimal price,
        BigDecimal stock
) {
}
