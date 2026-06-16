package ru.mrkotyaka.commonlibs.dto.delivery;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CourierRsDto(
        UUID courierId,
        UUID userId,
        String name,
        BigDecimal rating,
        List<DeliveryRsDto> deliveries
) {
}
