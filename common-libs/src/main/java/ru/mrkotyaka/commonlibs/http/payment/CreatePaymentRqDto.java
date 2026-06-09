package ru.mrkotyaka.commonlibs.http.payment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRqDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {
}
