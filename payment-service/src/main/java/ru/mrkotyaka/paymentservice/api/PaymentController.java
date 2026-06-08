package ru.mrkotyaka.paymentservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRequestDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentResponseDto;
import ru.mrkotyaka.paymentservice.domain.PaymentService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public CreatePaymentResponseDto createPayment(
            @RequestBody CreatePaymentRequestDto request
    ) {
        log.info("Received request: paymentRequest={}", request);

        return paymentService.makePayment(request);
    }
}
