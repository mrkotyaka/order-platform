package ru.mrkotyaka.storeservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductInStoreRsDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductRsDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreRsDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface StoreMapper {

    StoreEntity toEntity(StoreRqDto dto);

    @Mapping(source = "id", target = "storeId")
    @Mapping(target = "inventory", expression = "java(mapInventory(entity.getInventory()))")
    StoreRsDto toDto(StoreEntity entity);

    default Set<StoreProductInStoreRsDto> mapInventory(Set<StoreProductEntity> inventory) {
        if (inventory == null) {
            return Set.of();
        }
        return inventory.stream()
                .map(item -> StoreProductInStoreRsDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getPrice())
                        .stock(item.getStock())
                        .build()
                )
                .collect(Collectors.toSet());
    }
}
