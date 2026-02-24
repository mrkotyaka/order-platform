package ru.mrkotyaka.orderservice.domain;

import org.mapstruct.*;
import ru.mrkotyaka.orderservice.api.CreateOrderRequestDto;
import ru.mrkotyaka.orderservice.api.OrderDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {
    OrderEntity toOrderEntity(CreateOrderRequestDto request);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity, OrderDto orderDto) {
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);
}
