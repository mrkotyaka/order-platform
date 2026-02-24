package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {
    OrderEntity toOrderEntity(CreateOrderRequestDto request);

    @AfterMapping
    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity, OrderDto orderDto) {
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
    }

    OrderDto toOrderDto(OrderEntity orderEntity);
}
