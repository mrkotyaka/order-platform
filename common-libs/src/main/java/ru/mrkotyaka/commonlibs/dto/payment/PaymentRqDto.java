package ru.mrkotyaka.commonlibs.dto.payment;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.order.CashFlow;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRqDto(
        UUID orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        CashFlow cashFlow
) {
}
