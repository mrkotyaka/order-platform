package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRqDto;
import ru.mrkotyaka.commonlibs.dto.courier.CourierRsDto;
import ru.mrkotyaka.deliveryservice.domain.db.CourierEntity;
import ru.mrkotyaka.deliveryservice.domain.db.CourierMapper;
import ru.mrkotyaka.deliveryservice.domain.db.CourierRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier with id `%s` not found".formatted(id)));
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
}
