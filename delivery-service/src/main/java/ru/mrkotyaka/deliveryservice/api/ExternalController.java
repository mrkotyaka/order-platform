package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRsDto;
import ru.mrkotyaka.deliveryservice.domain.CourierProcessor;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalController {

    private final DeliveryProcessor deliveryProcessor;
    private final CourierProcessor courierProcessor;

    @PostMapping("/deliveries/getcourier")
    public UUID getCourierId(@RequestBody UUID orderId) {
        log.info("Getting courier by orderId {}", orderId);
        return deliveryProcessor.getDelivery(orderId).getCourierId().getUserId();
    }

    @PostMapping("/couriers/create")
    public CourierRsDto createCourier(@RequestBody CourierRqDto request) {
        log.info("Creating a new courier from external request");
        log.debug("Request details - userId: {}, name: {}", request.userId(), request.name());
        return courierProcessor.createCourier(request);
    }
}
