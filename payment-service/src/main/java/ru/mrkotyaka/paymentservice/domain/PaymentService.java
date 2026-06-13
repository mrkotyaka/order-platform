package ru.mrkotyaka.paymentservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;
import ru.mrkotyaka.commonlibs.enums.order.CashFlow;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentMethod;
import ru.mrkotyaka.commonlibs.enums.payment.PaymentStatus;
import ru.mrkotyaka.paymentservice.domain.db.PaymentEntity;
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

        if (request.cashFlow().equals(CashFlow.DEBIT)) {
            if (found.isPresent()) {
                log.info("Payment request already exists for order `{}`", request.orderId());
                return paymentMapper.toPaymentRsDto(found.get());
            }

            var payment = paymentMapper.toPaymentEntity(request);

            var paymentStatus = request.paymentMethod().equals(PaymentMethod.QR)
                    ? PaymentStatus.PAYMENT_FAILED
                    : PaymentStatus.PAYMENT_SUCCEEDED;

            log.warn("kilian.row: paymentStatus {}", paymentStatus);

            payment.setPaymentStatus(paymentStatus);

            log.warn("kilian.row: orderId {}", payment.getOrderId());

            return paymentMapper.toPaymentRsDto(paymentRepository.save(payment));

        } else if (request.cashFlow().equals(CashFlow.CREDIT)) {

            log.warn("kilian.row: cashFlow {}", request.cashFlow());

            PaymentStatus paymentStatus;
            if (found.isPresent()) {
                paymentStatus = found.get().getPaymentMethod().equals(PaymentMethod.QR)
                        ? PaymentStatus.PAYMENT_FAILED
                        : PaymentStatus.REFUNDED;
            } else {
                throw new RuntimeException("Payment not found");
            }

            var entity = new PaymentEntity();
            entity.setOrderId(request.orderId());
            entity.setPaymentStatus(paymentStatus);
            entity.setPaymentMethod(found.get().getPaymentMethod());
            entity.setAmount(request.amount().negate());

            return paymentMapper.toPaymentRsDto(paymentRepository.save(entity));
        }
        return null;
    }
}
