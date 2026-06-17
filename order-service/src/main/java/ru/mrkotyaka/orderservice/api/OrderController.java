package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderPaymentRqDto;
import ru.mrkotyaka.commonlibs.enums.order.OrderStatus;
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
        return orderProcessor.create(request, authUserId);
    }

    @GetMapping("/{id}")
    public OrderRsDto getOrderById(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.info("Retrieving order `{}` for user `{}`", id, authUserId);
        var found = orderProcessor.getOrderById(id);

        if (!found.getCustomerId().equals(authUserId) && authUserRole.equals("CUSTOMER")) {
            log.warn("User `{}` tried to access order `{}` belonging to user `{}`",
                    authUserId, id, found.getCustomerId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return orderMapper.toOrderDto(found);
    }

    @GetMapping
    public List<OrderRsDto> getAll(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all orders from the flow");
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting all orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all orders");
        }
        return orderProcessor.getAllOrders();
    }

    @GetMapping("/status")
    public List<OrderRsDto> getOrderByStatus(
            @RequestHeader("X-User-Roles") String authUserRole,
            @RequestParam OrderStatus orderStatus) {
        log.info("Retrieving `{}` orders", orderStatus);
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting orders");
        }
        return orderProcessor.getOrderByStatus(orderStatus);
    }

    @GetMapping("/pendingpayment")
    public List<OrderRsDto> getAllPending(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving all pending payment orders");
        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to getting all pending payment orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all pending payment orders");
        }
        return orderProcessor.getAllPendingPaymentOrders();
    }

    @PostMapping("/pay/{orderId}")
    public OrderRsDto payOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderPaymentRqDto request
    ) {
        log.info("Paying order `{}`", orderId);
        return orderProcessor.processPayment(orderId, request);
    }

    @PostMapping("/cancel/{orderId}")
    public OrderRsDto cancelOrder(@PathVariable UUID orderId) {
        log.info("Canceling order `{}`", orderId);
        return orderProcessor.cancelOrder(orderId);
    }
}
