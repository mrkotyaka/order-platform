package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
            @RequestBody CreateOrderRequestDto request,
            @RequestHeader("X-User-Id") Long authenticatedUserId
    ) {
        log.info("Processing the request in the flow: {}", Thread.currentThread());
        log.debug("Creating order: request={} for user `{}`", request, authenticatedUserId);
        var saved = orderProcessor.create(request, authenticatedUserId);
        return orderMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderDto getOne(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long authenticatedUserId
    ) {
        log.info("Retrieving order with id `{}` for user `{}`", id, authenticatedUserId);
        var found = orderProcessor.getOrderOrThrow(id);

        if(!found.getCustomerId().equals(authenticatedUserId)){
            log.warn("User `{}` tried to access order `{}` belonging to user `{}`",
                    authenticatedUserId, id, found.getCustomerId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return orderMapper.toOrderDto(found);
    }

    @PostMapping("/pay/{id}")
    public OrderDto payOrder(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request
    ) {
        log.debug("Paying order with id={}, request={}", id, request);
        var entity = orderProcessor.processPayment(id, request);
        return orderMapper.toOrderDto(entity);
    }

}
