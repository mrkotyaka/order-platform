package ru.mrkotyaka.commonlibs.kafka.delivery;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DeliveryAssignedEvent(
        UUID orderId,
        String courierName,
        Integer etaMinutes
) {
}
