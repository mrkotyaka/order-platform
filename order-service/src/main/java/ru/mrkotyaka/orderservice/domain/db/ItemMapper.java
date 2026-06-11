package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.item.ItemRqDto;
import ru.mrkotyaka.commonlibs.dto.item.ItemRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemRsDto toItemRsDto(ItemEntity itemEntity);

    ItemEntity toItemEntity(ItemRqDto request);
}
