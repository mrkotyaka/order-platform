package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.auth.UserRsDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRsDto;
import ru.mrkotyaka.deliveryservice.domain.CourierProcessor;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierProcessor courierProcessor;

    @GetMapping
    public List<CourierRsDto> getCouriers(
            @RequestHeader("X-User-Roles") String authUserRole
    ) {
        log.info("Retrieving couriers list");

        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to create new item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to create new item");
        }

        return courierProcessor.getFreeCouriers();
    }

    @PostMapping("/external")
    public CourierRsDto createCourier(
            @RequestBody CourierRqDto request
    ) {
        log.info("Creating a new courier from external request");
        log.info("Request details - userId: {}, name: {}", request.userId(), request.name());
        return courierProcessor.createCourier(request);
    }
}
