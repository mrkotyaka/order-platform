package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.deliveryservice.domain.db.*;

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
        var courierEntity = courierProcessor.getFreeAnyCourierOrThrow();

        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierId(courierEntity);
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 45));
        entity.setDeliveryDateTime(LocalDateTime.now());

        log.info("Saved order delivery was assigned.");

        return deliveryRepository.save(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        var courierEntity = courierProcessor.getCourierByIdOrThrow(assignedDelivery.getCourierId().getId());

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
}
