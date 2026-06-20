package ru.mrkotyaka.commonlibs.dto.stores;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.store.WarehouseEntryStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record WarehouseRqDto(
        WarehouseEntryStatus entryType,
        UUID storeId,
        UUID productId,
        BigDecimal price,
        BigDecimal stock
) {
}
