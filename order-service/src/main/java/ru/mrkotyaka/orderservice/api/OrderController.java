package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.http.order.CreateOrderRequestDto;
import ru.mrkotyaka.commonlibs.http.order.OrderDto;
import ru.mrkotyaka.commonlibs.http.order.OrderPaymentRequest;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;
import ru.mrkotyaka.orderservice.domain.db.OrderEntityMapper;

import java.util.List;

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
            @RequestHeader("X-User-Id") Long authenticatedUserId,
            @RequestHeader("X-User-Roles") String authenticatedUserRole
    ) {
        log.info("Retrieving order with id `{}` for user `{}`", id, authenticatedUserId);
        var found = orderProcessor.getOrderOrThrow(id);

        if(!found.getCustomerId().equals(authenticatedUserId) && authenticatedUserRole.equals("ROLE_USER")){
            log.warn("User `{}` tried to access order `{}` belonging to user `{}`",
                    authenticatedUserId, id, found.getCustomerId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return orderMapper.toOrderDto(found);
    }

    @GetMapping
    public List<OrderDto> getAll(
            @RequestHeader("X-User-Roles") String authenticatedUserRole){
        log.info("Retrieving all orders from the flow");
        if(authenticatedUserRole.equals("ROLE_USER")){
            log.warn("You are not is admin. Access denied to getting all orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all orders");
        }
        return orderProcessor.getAllOrders();
    }

    @GetMapping("/pendingpayment")
    public List<OrderDto> getAllPending(
            @RequestHeader("X-User-Roles") String authenticatedUserRole){
        log.info("Retrieving all pending payment orders from the flow");
        if(authenticatedUserRole.equals("ROLE_USER")){
            log.warn("You are not is admin. Access denied to getting all pending payment orders");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to getting all pending payment orders");
        }
        return orderProcessor.getAllPendingPaymentOrders();
    }

    @PostMapping("/pay/{id}")
    public OrderDto payOrder(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request,
            @RequestHeader("X-User-Roles") String authenticatedUserRole
    ) {
        log.debug("Paying order with id={}, request={}", id, request);
        if(authenticatedUserRole.equals("ROLE_USER")){
            log.warn("You are not is admin. Access denied to pay for this order");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to pay for this order");
        }
        var entity = orderProcessor.processPayment(id, request);
        return orderMapper.toOrderDto(entity);
    }

}
