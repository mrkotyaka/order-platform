package ru.mrkotyaka.commonlibs.dto.stores;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreProductRqDto(
        UUID storeId,
        UUID productId,
        BigDecimal price,
        BigDecimal stock
) {
}
