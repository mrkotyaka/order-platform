package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.item.ItemRsDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemEntityMapper {

    ItemRsDTO toItemDto(ItemEntity itemEntity);
}
