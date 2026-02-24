package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.orderservice.domain.db.OrderEntityMapper;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderEntityMapper orderMapper;

    @PostMapping
    public OrderDto create(
            @RequestBody CreateOrderRequestDto request
    ) {
        log.debug("Creating order: request={}", request);
        var saved = orderProcessor.create(request);
        return orderMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(
            @PathVariable Long id
    ) {
        log.info("Retrieving order with id `{}`", id);
        var found = orderProcessor.getOrderOrThrow(id);
        return orderMapper.toOrderDto(found);
    }
}
