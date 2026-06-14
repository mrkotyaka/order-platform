package ru.mrkotyaka.orderservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;

@FeignClient(
        name = "payment-service",
        url = "${payment-service.url}")
public interface PaymentHttpClient {

    @PostMapping("/api/external/payments/topay")
    PaymentRsDto doPayment(@RequestBody PaymentRqDto request);
}