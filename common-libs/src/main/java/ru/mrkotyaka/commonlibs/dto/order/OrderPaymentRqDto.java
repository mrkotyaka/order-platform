package ru.mrkotyaka.commonlibs.dto.order;

import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;

public record OrderPaymentRqDto(
        PaymentMethod paymentMethod
) {
}
