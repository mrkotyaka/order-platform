package ru.mrkotyaka.commonlibs.kafka;

import lombok.Builder;

@Builder
public record DeliveryAssignedEvent(
        Long orderId,
        String courierName,
        Integer etaMinutes
) {
}
