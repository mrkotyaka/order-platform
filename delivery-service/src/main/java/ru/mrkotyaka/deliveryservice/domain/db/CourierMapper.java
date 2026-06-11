package ru.mrkotyaka.deliveryservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRsDto;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourierMapper {

    CourierEntity toCourierEntity(CourierRqDto dto);

    CourierRsDto toCourierRsDto(CourierEntity entity);
}
