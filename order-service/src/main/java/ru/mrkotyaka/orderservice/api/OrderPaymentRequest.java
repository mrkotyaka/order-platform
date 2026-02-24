package ru.mrkotyaka.orderservice.api;

import ru.mrkotyaka.commonlibs.http.payment.PaymentMethod;

public record OrderPaymentRequest(
        PaymentMethod paymentMethod
) {
}
