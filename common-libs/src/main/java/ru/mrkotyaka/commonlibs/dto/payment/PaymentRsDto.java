package ru.mrkotyaka.commonlibs.dto.payment;

import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRsDto(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus
        ) {
}
