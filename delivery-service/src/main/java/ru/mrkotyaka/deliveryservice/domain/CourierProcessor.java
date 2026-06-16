package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.delivery.CourierRsDto;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;
import ru.mrkotyaka.deliveryservice.domain.db.CourierEntity;
import ru.mrkotyaka.deliveryservice.domain.db.CourierMapper;
import ru.mrkotyaka.deliveryservice.domain.db.CourierRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierProcessor {

    private final CourierRepository courierRepository;
    private final CourierMapper courierMapper;

    public List<CourierRsDto> getCouriers() {
        List<CourierRsDto> allCourierRsDto = new ArrayList<>();
        var entities = courierRepository.findAll();
        for (var entity : entities) {
            allCourierRsDto.add(courierMapper.toCourierRsDto(entity));
        }
        return allCourierRsDto;
    }

    public CourierEntity getCourierByIdOrThrow(UUID id) {
        var courierEntityOpt = courierRepository.findById(id);
        return courierEntityOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier with reviewId `%s` not found".formatted(id)));
    }

    private CourierEntity getCourierByUserIdOrThrow(UUID userId) {
        return courierRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Courier not found for userId: " + userId
                ));
    }

    public CourierEntity getFreeAnyCourierOrThrow() {
        var freeCourier = courierRepository.findOne();
        return freeCourier
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Free couriers not found now"));
    }

    public List<CourierRsDto> getFreeCouriers() {
        List<CourierRsDto> allCourierRsDto = new ArrayList<>();
        var allCourier = courierRepository.findAllFree();
        for (var courier : allCourier) {
            allCourierRsDto.add(courierMapper.toCourierRsDto(courier));
        }
        return allCourierRsDto;
    }

    public CourierRsDto createCourier(CourierRqDto request) {
        var courier = new CourierEntity();
        courier = courierMapper.toCourierEntity(request);
        courierRepository.save(courier);
        log.info("Courier saved successfully");
        return courierMapper.toCourierRsDto(courier);
    }

    public void updateCourierRating(ReviewRsDto event) {
        log.info("Updating courier rating for userId: {}, courierRating: {}",
                event.userId(), event.courierRating());

        var courier = getCourierByUserIdOrThrow(event.userId());

        var currentRating = courier.getRating();
        BigDecimal averageRating;

        if (currentRating == null) {
            averageRating = BigDecimal.valueOf(event.courierRating());
        } else {
            var eventRating = BigDecimal.valueOf(event.courierRating());
            var sum = currentRating.add(eventRating);
            averageRating = sum.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }


        courier.setRating(averageRating);
        courierRepository.save(courier);

        log.info("Courier rating updated: new rating = {}", averageRating);
    }

    public Map<String, Integer> getNumberDeliveries() {
        List<Object[]> results = courierRepository.findNumberDeliveriesByCourier();
        Map<String, Integer> deliveriesMap = new LinkedHashMap<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            deliveriesMap.put(name, count);
        }

        return deliveriesMap;
    }
}
