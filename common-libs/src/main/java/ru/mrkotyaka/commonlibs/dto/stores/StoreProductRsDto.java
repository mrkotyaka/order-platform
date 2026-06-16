package ru.mrkotyaka.commonlibs.dto.stores;

import java.math.BigDecimal;

public record StoreProductRsDto(
        String storeProductId,
        BigDecimal price,
        BigDecimal stock
) {
}
