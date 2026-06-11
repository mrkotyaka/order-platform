package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryProcessor deliveryProcessor;

    @PostMapping("/delivered/{id}")
    public OrderRsDto setDelivered(
            @PathVariable("id") UUID orderId,
            @RequestHeader("X-User-Id") UUID authUserId,
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.info("Changing the status of an order `{}`", orderId);
        var entity = deliveryProcessor.getDeliveryUserId(orderId);
        var deliveryUserId = entity.getCourierId().getUserId();
        if (!(authUserRole.equals("COURIER") && deliveryUserId.equals(authUserId)) && !authUserRole.equals("ADMIN")) {
            log.warn("You are not a courier. Access denied to changing the status");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to changing the status");
        }

        return deliveryProcessor.setStatusDelivered(entity);
    }
}
