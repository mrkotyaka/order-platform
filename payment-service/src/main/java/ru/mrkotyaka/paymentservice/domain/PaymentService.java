package ru.mrkotyaka.paymentservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.payment.*;
import ru.mrkotyaka.commonlibs.enums.order.CashFlow;
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

        if (request.cashFlow().equals(CashFlow.DEBIT) && found.isPresent()) {
            log.info("Payment request already exists for order `{}`", request.orderId());
            return paymentMapper.toPaymentRsDto(found.get());
        }

        if (request.cashFlow().equals(CashFlow.CREDIT) && found.isEmpty()) {
            log.info("Payment request not exists for order `{}`", request.orderId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
        }

        var payment = paymentMapper.toPaymentEntity(request);

        var paymentStatus = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : request.cashFlow().equals(CashFlow.DEBIT)
                  ? PaymentStatus.PAYMENT_SUCCEEDED
                  : request.cashFlow().equals(CashFlow.CREDIT)
                    ? PaymentStatus.REFUNDED
                    : null;
        var amount = request.cashFlow().equals(CashFlow.DEBIT)
                ? request.amount()
                : request.amount().negate();

        payment.setPaymentStatus(paymentStatus);
        payment.setAmount(amount);

        return paymentMapper.toPaymentRsDto(paymentRepository.save(payment));
    }
}
