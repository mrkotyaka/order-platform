package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.store.WarehouseEntryStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record WarehouseRsDto(
        WarehouseEntryStatus entryType,
        UUID storeId,
        String storeName,
        UUID productId,
        String productName,
        BigDecimal price,
        BigDecimal stock
) {
}
