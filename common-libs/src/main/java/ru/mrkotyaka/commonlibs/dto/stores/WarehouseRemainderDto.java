package ru.mrkotyaka.commonlibs.dto.stores;

import java.math.BigDecimal;
import java.util.UUID;

public record WarehouseRemainderDto(
        UUID storeId,
        UUID productId,
        BigDecimal remainder
) {
}
