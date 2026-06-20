package ru.mrkotyaka.storeservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.dto.order.OrderItemRsDto;
import ru.mrkotyaka.commonlibs.dto.order.PriceRequestDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalController {

    private final StoreProcessor storeProcessor;

    @PostMapping("/warehouse/itemprice")
    Set<OrderItemRsDto> getItemPrice(@RequestBody PriceRequestDto request) {
        return storeProcessor.getItemPrice(request);
    }
}
