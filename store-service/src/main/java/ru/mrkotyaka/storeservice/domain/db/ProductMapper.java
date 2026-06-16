package ru.mrkotyaka.storeservice.domain.db;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRqDto;
import ru.mrkotyaka.commonlibs.dto.stores.ProductRsDto;
import ru.mrkotyaka.commonlibs.dto.stores.StoreProductInProductRsDto;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    ProductEntity toEntity(ProductRqDto dto);

    @Mapping(source = "id", target = "productId")
    @Mapping(target = "storeProducts", expression = "java(mapStoreProducts(entity.getStoreProducts()))")
    ProductRsDto toDto(ProductEntity entity);

    default Set<StoreProductInProductRsDto> mapStoreProducts(Set<StoreProductEntity> storeProducts) {
        if (storeProducts == null) return Set.of();

        return storeProducts.stream()
                .map(item -> StoreProductInProductRsDto.builder()
                        .storeId(item.getStore().getId())
                        .storeName(item.getStore().getName())
                        .price(item.getPrice())
                        .stock(item.getStock())
                        .build())
                .collect(Collectors.toSet());
    }
}
