package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalController {

    private final OrderProcessor orderProcessor;

    @PostMapping("/orders/delivered/{id}")
    public OrderRsDto setStatusDelivered(@PathVariable("id") UUID orderId) {
        return orderProcessor.processDeliveredState(orderId);
    }

    @PostMapping("/orders/checkowner")
    public boolean canForReview(@RequestParam("userId") UUID userId, @RequestParam("orderId") UUID orderId) {
        log.info("canForReview: userId={}, orderId={}", userId, orderId);
        return orderProcessor.canForReview(userId, orderId);
    }
}
