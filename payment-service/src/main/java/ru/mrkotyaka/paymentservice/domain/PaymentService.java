package ru.mrkotyaka.paymentservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.http.payment.*;
import ru.mrkotyaka.paymentservice.domain.db.PaymentEntityMapper;
import ru.mrkotyaka.paymentservice.domain.db.PaymentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEntityMapper paymentMapper;

    public CreatePaymentRsDto makePayment(CreatePaymentRqDto request) {

        var found = paymentRepository.findByOrderId(request.orderId());
        if (found.isPresent()) {
            log.info("Payment request already exists: orderId={}", request.orderId());

            return paymentMapper.toResponseDTO(found.get());
        }

        var entity = paymentMapper.toEntity(request);

        var paymentStatus = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;

        entity.setPaymentStatus(paymentStatus);

        var savedEntity = paymentRepository.save(entity);
        return paymentMapper.toResponseDTO(savedEntity);
    }
}
