package ru.mrkotyaka.storeservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductEntity toEntity(ProductRqDto dto);

    @Mapping(source = "id", target = "productId")
    ProductRsDto toDto(ProductEntity entity);
}
