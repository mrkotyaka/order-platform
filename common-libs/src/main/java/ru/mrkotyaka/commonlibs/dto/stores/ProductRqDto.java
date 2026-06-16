package ru.mrkotyaka.commonlibs.dto.stores;

import ru.mrkotyaka.commonlibs.enums.store.ProductCategory;

import java.math.BigDecimal;

public record ProductRqDto(
        String name,
        String description,
        ProductCategory category
) {
}
