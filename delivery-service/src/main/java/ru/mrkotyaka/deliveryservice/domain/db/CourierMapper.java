package ru.mrkotyaka.deliveryservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRsDto;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourierMapper {

    CourierEntity toCourierEntity(CourierRqDto dto);

    @Mapping(source = "id", target = "courierId")
    CourierRsDto toCourierRsDto(CourierEntity entity);
}
