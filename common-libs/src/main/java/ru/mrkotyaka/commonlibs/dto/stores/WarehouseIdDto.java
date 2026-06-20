package ru.mrkotyaka.commonlibs.dto.stores;

import java.util.UUID;

public record WarehouseIdDto(
        UUID storeId,
        UUID productId
) {
}
