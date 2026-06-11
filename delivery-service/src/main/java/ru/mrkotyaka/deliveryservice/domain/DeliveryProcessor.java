package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.deliveryservice.domain.db.*;
import ru.mrkotyaka.deliveryservice.external.OrderHttpClient;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProcessor {

    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<UUID, DeliveryAssignedEvent> kafkaTemplate;
    private final CourierProcessor courierProcessor;
    private final OrderHttpClient orderHttpClient;

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

    private DeliveryEntity assignDelivery(UUID orderId) {
        var freeCourier = courierProcessor.getFreeAnyCourierOrThrow();

        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierId(freeCourier);
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 45));
        entity.setCreatedAt(LocalDateTime.now());

        log.info("Saved order delivery was assigned.");

        return deliveryRepository.save(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        var courierEntity = courierProcessor.getCourierByIdOrThrow(assignedDelivery.getCourierId().getId());

        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .userId(assignedDelivery.getCourierId().getUserId())
                        .courierName(courierEntity.getName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        ).thenAccept(result -> {
            log.info("Delivery `{}` assigned", assignedDelivery.getId());
        });
    }

    public DeliveryEntity getDeliveryUserId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                {
                    log.info("Delivery with orderId `{}` not found", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Delivery for order `%s` not found".formatted(orderId));
                });
    }

    public OrderRsDto setStatusDelivered(DeliveryEntity entity) {
        var delivery = orderHttpClient.setStatusDelivered(entity.getOrderId());
        entity.setDeliveredAt(delivery.deliveredAt());
        deliveryRepository.save(entity);
        log.info("Delivery `{}` is delivered", entity.getId());
        return delivery;
    }
}
