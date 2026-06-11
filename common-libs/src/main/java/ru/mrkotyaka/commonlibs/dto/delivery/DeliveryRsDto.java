package ru.mrkotyaka.commonlibs.dto.delivery;

import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryRsDto(
        UUID id,
        UUID orderId,
        CourierRqDto courierId,
        Integer etaMinutes,
        LocalDateTime deliveredAt,
        LocalDateTime deliveryDateTime
) {
}
