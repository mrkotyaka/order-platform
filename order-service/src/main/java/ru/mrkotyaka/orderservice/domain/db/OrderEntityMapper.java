package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.commonlibs.http.order.OrderItemDto;
import ru.mrkotyaka.commonlibs.http.order.OrderItemRequestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

    OrderEntity toOrderEntity(CreateOrderRequestDto request);

    OrderItemEntity toOrderItemEntity(OrderItemRequestDto request);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
        orderEntity
                .getItems()
                .forEach(
                        orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);

    OrderItemDto toOrderItemDto(OrderItemEntity entity);

//    OrderItemRequestDto toOrderItemRequestDto(OrderItemEntity entity);
}
