package ru.mrkotyaka.reviewservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "delivery-service",
        url = "${delivery-service.url}")
public interface DeliveryHttpClient {
    @PostMapping("/api/external/deliveries/getcourier")
    UUID getCourierId(@RequestBody UUID orderId);
}
