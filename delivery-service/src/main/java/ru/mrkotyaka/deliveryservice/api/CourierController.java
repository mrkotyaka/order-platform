package ru.mrkotyaka.deliveryservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRsDto;
import ru.mrkotyaka.deliveryservice.domain.CourierProcessor;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierProcessor courierProcessor;

    @GetMapping
    public List<CourierRsDto> getCouriers(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving couriers list");

        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to create new item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to create new item");
        }

        return courierProcessor.getCouriers();
    }

    @GetMapping("/free")
    public List<CourierRsDto> getFreeCouriers(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving list of free couriers");

        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to create new item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to create new item");
        }
        return courierProcessor.getFreeCouriers();
    }

    @GetMapping("/numberdeliveries")
    public Map<String, Integer> getNumberDeliveries(@RequestHeader("X-User-Roles") String authUserRole) {
        log.info("Retrieving list of numbers of deliveries by couriers");

        if (!authUserRole.equals("ADMIN")) {
            log.warn("You are not is admin. Access denied to create new item");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to create new item");
        }
        return courierProcessor.getNumberDeliveries();
    }
}
