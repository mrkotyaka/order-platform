package ru.mrkotyaka.orderservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "delivery-service",
        url = "${delivery-service.url}")
public interface DeliveryHttpClient {
    @PostMapping("/api/deliveries/external/getcourier")
    UUID getCourierId(@RequestBody UUID orderId);
}
