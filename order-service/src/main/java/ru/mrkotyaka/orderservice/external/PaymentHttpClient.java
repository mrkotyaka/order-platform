package ru.mrkotyaka.orderservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRqDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRsDto;

@FeignClient(
        name = "payment-service",
        url = "${payment-service.base-url}")
public interface PaymentHttpClient {

    @PostMapping("/api/payments")
    CreatePaymentRsDto createPayment(@RequestBody CreatePaymentRqDto request);
}