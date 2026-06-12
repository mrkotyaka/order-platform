package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries/external")
public class ExternalController {

    private final DeliveryProcessor deliveryProcessor;

    @PostMapping("/getcourier")
    UUID getCourierId(@RequestBody UUID orderId){
        return deliveryProcessor.getDelivery(orderId).getCourierId().getUserId();
    }
}
