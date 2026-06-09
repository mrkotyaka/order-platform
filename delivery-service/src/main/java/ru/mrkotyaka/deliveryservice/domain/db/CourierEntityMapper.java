package ru.mrkotyaka.deliveryservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.delivery.CourierRsDto;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourierEntityMapper {
    CourierEntity toCourierEntity(CourierRsDto response);
    CourierRsDto toCourierDTO(CourierEntity entity);
}
