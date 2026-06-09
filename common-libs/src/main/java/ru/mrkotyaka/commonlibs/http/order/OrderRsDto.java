package ru.mrkotyaka.commonlibs.http.order;

import java.math.BigDecimal;
import java.util.Set;

public record OrderRsDto(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemRsDto> items
) {
}
