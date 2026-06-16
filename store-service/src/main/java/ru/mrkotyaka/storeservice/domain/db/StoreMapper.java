package ru.mrkotyaka.storeservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductRsDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface StoreMapper {

    StoreEntity toEntity(StoreRqDto dto);

    @Mapping(source = "id", target = "storeId")
    StoreRsDto toDto(StoreEntity entity);

    StoreProductEntity toStoreProductEntity(StoreProductRsDto dto);

    @AfterMapping
    default void linkStoreProductEntities(@MappingTarget StoreEntity entity) {
        entity
                .getInventory()
                .forEach(
                        storeProductEntity -> storeProductEntity.setStore(entity));

    }
}
