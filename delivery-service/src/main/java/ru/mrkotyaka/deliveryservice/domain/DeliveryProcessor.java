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
import ru.mrkotyaka.deliveryservice.domain.db.DeliveryEntity;
import ru.mrkotyaka.deliveryservice.domain.db.DeliveryRepository;
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
        var entity = deliveryRepository.findByOrderId(orderId);

        if (entity.isPresent()) {
            log.warn("Found order delivery was already assigned: delivery={}", entity.get());
            return;
        }

        var assignedDelivery = assignDelivery(orderId);
        sendDeliveryAssignedEvent(assignedDelivery);
    }

    private DeliveryEntity assignDelivery(UUID orderId) {
        var freeCourier = courierProcessor.getFreeAnyCourier();

        var entity = DeliveryEntity.builder()
                .orderId(orderId)
                .courierId(freeCourier)
                .etaMinutes(ThreadLocalRandom.current().nextInt(10, 45))
                .build();

        var saved = deliveryRepository.save(entity);
        log.info("Saved delivery `{}` was assigned", saved.getId());
        return saved;
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        var entity = courierProcessor.getCourierById(assignedDelivery.getCourierId().getId());

        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .userId(assignedDelivery.getCourierId().getUserId())
                        .courierName(entity.getName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .canceledAt(assignedDelivery.getCanceledAt())
                        .build()
        ).thenAccept(result -> log.info("Delivery `{}` assigned", assignedDelivery.getId()));
    }

    public DeliveryEntity getDelivery(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Delivery for order `%s` not found".formatted(orderId)));
    }

    public OrderRsDto setStatusDelivered(DeliveryEntity delivery) {
        var orderDto = orderHttpClient.setStatusDelivered(delivery.getOrderId());
        delivery.setDeliveredAt(orderDto.deliveredAt());
        var saved = deliveryRepository.save(delivery);
        log.info("Delivery `{}` is delivered", saved.getId());
        return orderDto;
    }

    public void processOrderCanceled(OrderPaidEvent event) {
        var orderId = event.orderId();
        var delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Delivery for order `%s` not found".formatted(orderId)));

        delivery.setCanceledAt(LocalDateTime.now());

        var saved = deliveryRepository.save(delivery);
        log.info("Delivery `{}` is canceled", saved.getId());
        sendDeliveryAssignedEvent(saved);
    }
}
