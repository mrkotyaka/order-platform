package ru.mrkotyaka.paymentservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;
import ru.mrkotyaka.paymentservice.domain.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments/external")
public class ExternalController {

    private final PaymentService paymentService;

    @PostMapping("/topay")
    public PaymentRsDto doPayment(
            @RequestBody PaymentRqDto request
    ) {
        log.info("Received request: do pay={}", request);
        return paymentService.makePayment(request);
    }
}
