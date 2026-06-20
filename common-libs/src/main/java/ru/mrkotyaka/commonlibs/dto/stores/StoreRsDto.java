package ru.mrkotyaka.commonlibs.dto.stores;

import java.util.Set;
import java.util.UUID;

public record StoreRsDto(
        UUID storeId,
        String name,
        String address,
        int rating,
        Set<WarehouseInStoreRsDto> inventory
) {
}
