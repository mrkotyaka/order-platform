package ru.mrkotyaka.commonlibs.dto.order;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
public record OrderRsDto(
        UUID orderId,
        UUID customerId,
        String comment,
        String deliveryAddress,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        LocalDateTime deliveredAt,
        OrderStatus orderStatus,
        String storeName,
        Set<OrderItemRsDto> items
) {
}
