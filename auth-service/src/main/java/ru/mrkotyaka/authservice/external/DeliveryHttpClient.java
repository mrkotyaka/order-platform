package ru.mrkotyaka.authservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRsDto;

@FeignClient(
        name = "delivery-service",
        url = "${delivery-service.url}")
public interface DeliveryHttpClient {

    @PostMapping("/api/external/couriers/create")
    CourierRsDto createCourier(@RequestBody CourierRqDto request);
}
