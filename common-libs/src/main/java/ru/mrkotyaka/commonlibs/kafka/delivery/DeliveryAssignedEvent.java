package ru.mrkotyaka.commonlibs.kafka.delivery;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record DeliveryAssignedEvent(
        UUID orderId,
        UUID userId,
        String courierName,
        Integer etaMinutes,
        LocalDateTime canceledAt
) {
}
