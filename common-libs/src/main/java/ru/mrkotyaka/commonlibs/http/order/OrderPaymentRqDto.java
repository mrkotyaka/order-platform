package ru.mrkotyaka.commonlibs.http.order;

import ru.mrkotyaka.commonlibs.http.payment.PaymentMethod;

public record OrderPaymentRqDto(
        PaymentMethod paymentMethod
) {
}
