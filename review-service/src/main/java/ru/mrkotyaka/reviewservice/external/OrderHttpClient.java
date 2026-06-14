package ru.mrkotyaka.reviewservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "order-service",
        url = "${order-service.url}")
public interface OrderHttpClient {
    @PostMapping("/api/external/orders/checkowner")
    boolean canForReview(@RequestParam("userId") UUID userId, @RequestParam("orderId") UUID orderId);
}
