package ru.mrkotyaka.orderservice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.orderservice.domain.db.OrderEntity;
import ru.mrkotyaka.orderservice.domain.db.OrderEntityMapper;
import ru.mrkotyaka.orderservice.domain.db.OrderItemEntity;
import ru.mrkotyaka.orderservice.domain.db.OrderRepository;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
public class OrderProcessor {
    private final OrderRepository orderRepository;
    private final OrderEntityMapper orderMapper;

    public OrderEntity create(CreateOrderRequestDto request) {
        var entity = orderMapper.toOrderEntity(request);
        calcPricingForOrder(entity);
        entity.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        return orderRepository.save(entity);
    }

    public OrderEntity getOrderOrThrow(Long id) {
        var orderItemEntityOpt = orderRepository.findById(id);
        return orderItemEntityOpt.orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }



    private void calcPricingForOrder(OrderEntity orderEntity) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(OrderItemEntity item: orderEntity.getItems()){
            var randomPrice = ThreadLocalRandom.current().nextDouble(100,5000);
            item.setPriceAtPurchase(BigDecimal.valueOf(randomPrice));

            totalPrice = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())).add(totalPrice);

        }
        orderEntity.setTotalAmount(totalPrice);
    }

    public OrderEntity processPayment(OrderPaymentRequest request) {

    }
}
