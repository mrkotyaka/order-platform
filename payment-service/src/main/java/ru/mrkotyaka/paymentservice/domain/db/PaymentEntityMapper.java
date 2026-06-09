package ru.mrkotyaka.paymentservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRqDto;
import ru.mrkotyaka.commonlibs.http.payment.CreatePaymentRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(CreatePaymentRqDto request);

    @Mapping(source = "id", target = "paymentId")
    CreatePaymentRsDto toResponseDTO(PaymentEntity paymentEntity);
}
