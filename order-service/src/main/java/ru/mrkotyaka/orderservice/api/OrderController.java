package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderPaymentRqDto;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;
import ru.mrkotyaka.orderservice.domain.db.OrderMapper;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessor orderProcessor;
    private final OrderMapper orderMapper;

    @PostMapping
    public OrderRsDto create(
            @RequestBody OrderRqDto request,
            @RequestHeader("X-User-Id") UUID authUserId
    ) {
        log.debug("Processing the request: {}", Thread.currentThread());
        log.info("Creating order for user `{}`", authUserId);
        var saved = orderProcessor.create(request, authUserId);
        return orderMapper.toOrderDto(saved);
    }

    @GetMapping("/{id}")
    public OrderRsDto getOne(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.info("Retrieving order `{}` for user `{}`", id, authUserId);
        var found = orderProcessor.getOrderOrThrow(id);

        if (!found.getCustomerId().equals(authUserId) && authUserRole.equals("CUSTOMER")) {
            log.warn("User `{}` tried to access order `{}` belonging to user `{}`",
                    authUserId, id, found.getCustomerId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return orderMapper.toOrderDto(found);
    }

    @GetMapping
    public List<OrderRsDto> getAll(
            @RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all orders from the flow");
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting all orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all orders");
        }
        return orderProcessor.getAllOrders();
    }

    @GetMapping("/pendingpayment")
    public List<OrderRsDto> getAllPending(
            @RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all pending payment orders from the flow");
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting all pending payment orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all pending payment orders");
        }
        return orderProcessor.getAllPendingPaymentOrders();
    }

    @PostMapping("/pay/{id}")
    public OrderRsDto payOrder(
            @PathVariable UUID id,
            @RequestBody OrderPaymentRqDto request,
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.debug("Paying order `{}`", id);
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to pay for this order");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to pay for this order");
        }
        var entity = orderProcessor.processPayment(id, request);
        return orderMapper.toOrderDto(entity);
    }

    @PostMapping("/delivered/external/{id}")
    public OrderRsDto setStatusDelivered(@PathVariable("id") UUID orderId){
        var entity = orderProcessor.processDeliveredState(orderId);
        return orderMapper.toOrderDto(entity);
    }
}
