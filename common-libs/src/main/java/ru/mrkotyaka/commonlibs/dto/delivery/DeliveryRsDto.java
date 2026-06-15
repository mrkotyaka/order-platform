package ru.mrkotyaka.commonlibs.dto.delivery;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryRsDto(
        UUID deliveryId,
        UUID orderId,
        Integer etaMinutes,
        LocalDateTime createdAt,
        LocalDateTime deliveredAt,
        LocalDateTime canceledAt
) {
}
