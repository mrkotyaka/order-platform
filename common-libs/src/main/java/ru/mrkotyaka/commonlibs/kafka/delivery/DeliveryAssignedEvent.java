package ru.mrkotyaka.commonlibs.kafka.delivery;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryAssignedEvent(
        UUID orderId,
        UUID userId,
        String courierName,
        Integer etaMinutes
) {
}
