package ru.mrkotyaka.orderservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRqDto;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRsDto;
import ru.mrkotyaka.commonlibs.dto.order.PriceRequestDto;

import java.util.Set;

@FeignClient(
        name = "store-service",
        url = "${store-service.url}")
public interface StoreHttpClient {

    @PostMapping("/api/external/storeproducts/itemprice")
    Set<OrderItemRsDto> getItemPrice(@RequestBody PriceRequestDto request);
}
