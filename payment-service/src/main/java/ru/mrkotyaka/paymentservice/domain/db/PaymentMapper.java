package ru.mrkotyaka.paymentservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRqDto;
import ru.mrkotyaka.commonlibs.dto.payment.PaymentRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    PaymentEntity toPaymentEntity(PaymentRqDto request);

    @Mapping(source = "id", target = "paymentId")
    PaymentRsDto toPaymentRsDto(PaymentEntity paymentEntity);
}
