package ru.mrkotyaka.commonlibs.dto.order;

import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record OrderRsDto(
        UUID id,
        UUID customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemRsDto> items
) {
}
