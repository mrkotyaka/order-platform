package ru.mrkotyaka.commonlibs.dto.courier;

import ru.mrkotyaka.commonlibs.dto.delivery.DeliveryRsDto;

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
