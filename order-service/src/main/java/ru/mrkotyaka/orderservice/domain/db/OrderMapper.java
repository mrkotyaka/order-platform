package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.dto.order.OrderRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRsDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderEntity toOrderEntity(OrderRqDto request);

    OrderItemEntity toOrderItemEntity(OrderItemRqDto request);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(
                        orderItemEntity -> orderItemEntity.setOrderId(orderEntity));
    }

    OrderRsDto toOrderDto(OrderEntity orderEntity);

    OrderItemRsDto toOrderItemDto(OrderItemEntity entity);
}
