package ru.mrkotyaka.commonlibs.dto.stores;

import java.util.Set;

public record StoreRsDto(
        String storeId,
        String name,
        String address,
        int rating,
        Set<StoreProductRsDto> inventory
) {
}
