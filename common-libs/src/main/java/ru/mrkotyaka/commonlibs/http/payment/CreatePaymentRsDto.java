package ru.mrkotyaka.commonlibs.http.payment;

import java.math.BigDecimal;

public record CreatePaymentRsDto(
        Long paymentId,
        PaymentStatus paymentStatus,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount) {
}
