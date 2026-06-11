package ru.mrkotyaka.commonlibs.dto.delivery;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryRsDto(
        UUID id,
        UUID orderId,
        Integer etaMinutes,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt
) {
}
