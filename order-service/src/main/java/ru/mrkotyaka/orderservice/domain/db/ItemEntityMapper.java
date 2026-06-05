package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.http.item.ItemDTO;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemEntityMapper {

    ItemDTO toItemDto(ItemEntity itemEntity);
}
