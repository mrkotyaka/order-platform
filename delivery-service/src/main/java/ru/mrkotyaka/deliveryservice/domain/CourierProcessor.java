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

    public CourierEntity getCourierByIdOrThrow(UUID id) {
        var courierEntityOpt = courierRepository.findById(id);
        return courierEntityOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier with id `%s` not found".formatted(id)));
    }

    public CourierEntity getFreeAnyCourierOrThrow() {
        var courierEntityOpt = courierRepository.findOneFree(LocalDateTime.now());
        return courierEntityOpt
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Free couriers not found now"));
    }

    public List<CourierRsDto> getFreeCouriers() {

        List<CourierRsDto> allCourierRsDto = new ArrayList<>();
        var allCourier = courierRepository.findAllFree(LocalDateTime.now());
        for (var courier : allCourier) {
            allCourierRsDto.add(courierMapper.toCourierRsDto(courier));
        }
        return allCourierRsDto;
    }

    public CourierRsDto createCourier(CourierRqDto request) {
        log.info("create entity");
        var courier = new CourierEntity();

        log.info("=== CREATE COURIER START ===");
        log.info("Request userId: {}", request.userId());
        log.info("Request name: '{}'", request.name());

        log.info("mapping dto to entity");
        courier = courierMapper.toCourierEntity(request);

        log.info("After mapping - entity name: '{}'", courier.getName());
        log.info("After mapping - entity userId: '{}'", courier.getUserId());

        if (courier.getName() == null) {
            log.error("ERROR: Courier name is NULL after mapping!");
        }

        log.info("saving entity");
        courierRepository.save(courier);

        log.info("responding dto");
        return courierMapper.toCourierRsDto(courier);
    }
}
