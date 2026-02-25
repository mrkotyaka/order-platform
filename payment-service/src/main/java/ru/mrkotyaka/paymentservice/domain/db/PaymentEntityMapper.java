package ru.mrkotyaka.paymentservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRequestDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentResponseDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(CreatePaymentRequestDto request);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentResponseDto toResponseDTO(PaymentEntity paymentEntity);
}
