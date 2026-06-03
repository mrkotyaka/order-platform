package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.deliveryservice.domain.CourierEntity;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final DeliveryProcessor deliveryProcessor;

    @GetMapping
    public Optional<List<CourierEntity>> getCouriers() {
        log.info("Retrieving couriers list");

        return deliveryProcessor.getFreeCouriers();
    }
}
