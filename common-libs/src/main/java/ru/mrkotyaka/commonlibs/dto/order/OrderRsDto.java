package ru.mrkotyaka.commonlibs.dto.order;

import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OrderRsDto(
        UUID orderId,
        UUID customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        LocalDateTime deliveredAt,
        OrderStatus orderStatus,
        Set<OrderItemRsDto> items
) {
}
