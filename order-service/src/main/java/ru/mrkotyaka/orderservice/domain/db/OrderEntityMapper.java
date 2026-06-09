package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.http.order.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

    OrderEntity toOrderEntity(CreateOrderRqDto request);

    OrderItemEntity toOrderItemEntity(OrderItemRqDto request);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(
                        orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderRsDto toOrderDto(OrderEntity orderEntity);

    OrderItemRsDto toOrderItemDto(OrderItemEntity entity);

//    OrderItemRequestDto toOrderItemRequestDto(OrderItemEntity entity);
}
