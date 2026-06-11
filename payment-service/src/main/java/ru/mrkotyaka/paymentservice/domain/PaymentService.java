package ru.mrkotyaka.paymentservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.dto.payment.*;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentStatus;
import ru.mrkotyaka.paymentservice.domain.db.PaymentMapper;
import ru.mrkotyaka.paymentservice.domain.db.PaymentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentRsDto makePayment(PaymentRqDto request) {

        var found = paymentRepository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.info("Payment request already exists for order `{}`", request.orderId());

            return paymentMapper.toPaymentRsDto(found.get());
        }

        var payment = paymentMapper.toPaymentEntity(request);

        var paymentStatus = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        payment.setPaymentStatus(paymentStatus);

        return paymentMapper.toPaymentRsDto(paymentRepository.save(payment));
    }
}
