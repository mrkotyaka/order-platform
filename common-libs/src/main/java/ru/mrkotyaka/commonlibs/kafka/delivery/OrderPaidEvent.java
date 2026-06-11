package ru.mrkotyaka.commonlibs.kafka.delivery;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderPaidEvent(
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
