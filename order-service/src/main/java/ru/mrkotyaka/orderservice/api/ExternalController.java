package ru.mrkotyaka.orderservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/external")
public class ExternalController {

    private final OrderProcessor orderProcessor;

    @PostMapping("/delivered/{id}")
    public OrderRsDto setStatusDelivered(@PathVariable("id") UUID orderId) {
        return orderProcessor.processDeliveredState(orderId);
    }
}
