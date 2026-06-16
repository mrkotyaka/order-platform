package ru.mrkotyaka.commonlibs.dto.stores;

import ru.mrkotyaka.commonlibs.enums.store.ProductCategory;

import java.math.BigDecimal;
import java.util.Set;

public record ProductRsDto(
        String productId,
        String name,
        String description,
        BigDecimal price,
        ProductCategory category,
        Set<StoreProductRsDto> storeProducts
) {
}
