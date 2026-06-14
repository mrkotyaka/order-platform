package ru.mrkotyaka.deliveryservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        url = "${order-service.url}")
public interface OrderHttpClient {
    @PostMapping("/api/external/orders/delivered/{id}")
    OrderRsDto setStatusDelivered(@PathVariable("id") UUID orderId);
}
