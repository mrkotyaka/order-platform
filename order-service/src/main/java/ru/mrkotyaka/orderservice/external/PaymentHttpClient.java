package ru.mrkotyaka.orderservice.external;

import org.springframework.web.bind.annotation.RequestBody;import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRequestDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentResponseDto;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    CreatePaymentResponseDto createPayment(@RequestBody CreatePaymentRequestDto request);
}
