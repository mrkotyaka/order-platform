package ru.mrkotyaka.commonlibs.dto.stores;

import ru.mrkotyaka.commonlibs.enums.store.ProductCategory;

import java.util.Set;

public record ProductRsDto(
        String productId,
        String name,
        String description,
        ProductCategory category,
        Set<WarehouseInProductRsDto> storeProducts
) {
}
