package ru.mrkotyaka.orderservice.domain.db;

import org.mapstruct.*;
import ru.mrkotyaka.commonlibs.dto.order.OrderRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRsDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderEntity toOrderEntity(OrderRqDto request);

    OrderItemEntity toOrderItemEntity(OrderItemRsDto request);

//    @AfterMapping
//    default void linkOrderItemEntities(@MappingTarget OrderEntity orderEntity) {
//        orderEntity
//                .getItems()
//                .forEach(
//                        orderItemEntity -> orderItemEntity.setOrderId(orderEntity));
//    }

    @Mapping(source = "id", target = "orderId")
    OrderRsDto toOrderRsDto(OrderEntity orderEntity);

//    OrderItemRsDto toOrderItemDto(OrderItemEntity entity);
}
