package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.commonlibs.http.delivery.CourierRsDto;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final DeliveryProcessor deliveryProcessor;

    @GetMapping
    public List<CourierRsDto> getCouriers() {
        log.info("Retrieving couriers list");

        return deliveryProcessor.getFreeCouriers();
    }
}
