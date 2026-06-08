package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.http.delivery.CourierDTO;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.deliveryservice.domain.db.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryRepository deliveryRepository;
    private final CourierRepository courierRepository;
    private final CourierEntityMapper courierMapper;
    private final KafkaTemplate<Long, DeliveryAssignedEvent> kafkaTemplate;

    @Value("${delivery-assigned-topic}")
    private String deliveryAssignedTopic;

    public void processOrderPaid(OrderPaidEvent event) {

        var orderId = event.orderId();
        var found = deliveryRepository.findByOrderId(orderId);
        if (found.isPresent()) {
            log.info("Found order delivery was already assigned: delivery={}", found.get());
            return;
        }

        var assignedDelivery = assignDelivery(orderId);
        sendDeliveryAssignedEvent(assignedDelivery);
    }

    private DeliveryEntity assignDelivery(Long orderId) {
        var courierEntity = getFreeAnyCourierOrThrow();

        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierId(courierEntity);
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 45));
        entity.setDeliveryDateTime(LocalDateTime.now());

        log.info("Saved order delivery was assigned: delivery={}", entity);

        return deliveryRepository.save(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        var courierEntity = getCourierByIdOrThrow(assignedDelivery.getCourierId().getId());

        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .courierName(courierEntity.getName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        ).thenAccept(result -> {
            log.info("Delivery assigned to delivery={}", assignedDelivery.getId());
        });
    }

    public CourierEntity getCourierByIdOrThrow(Long id) {
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

    public List<CourierDTO> getFreeCouriers() {

        List<CourierDTO> allCourierDTO = new ArrayList<>();
        var allCourier = courierRepository.findAllFree(LocalDateTime.now());
        for (var courier : allCourier) {
            allCourierDTO.add(courierMapper.toCourierDTO(courier));
        }
        return allCourierDTO;
    }
}
