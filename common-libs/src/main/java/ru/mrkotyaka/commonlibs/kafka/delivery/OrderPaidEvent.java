package ru.mrkotyaka.commonlibs.kafka.delivery;

import lombok.Builder;
import ru.mrkotyaka.commonlibs.http.payment.PaymentMethod;

import java.math.BigDecimal;

@Builder
public record OrderPaidEvent(
        Long orderId,
        Long paymentId,
        BigDecimal amount,
        PaymentMethod paymentMethod
) {
}
