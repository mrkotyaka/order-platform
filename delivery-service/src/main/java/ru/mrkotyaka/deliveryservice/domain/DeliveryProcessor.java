package ru.mrkotyaka.deliveryservice.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mrkotyaka.commonlibs.kafka.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.OrderPaidEvent;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DeliveryProcessor {
    private final DeliveryRepository deliveryRepository;
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
        var entity = new DeliveryEntity();
        entity.setOrderId(orderId);
        entity.setCourierName("courier-" + ThreadLocalRandom.current().nextInt(100));
        entity.setEtaMinutes(ThreadLocalRandom.current().nextInt(10, 45));

        log.info("Saved order delivery was assigned: delivery={}", entity);
        return deliveryRepository.save(entity);
    }

    private void sendDeliveryAssignedEvent(DeliveryEntity assignedDelivery) {
        kafkaTemplate.send(
                deliveryAssignedTopic,
                assignedDelivery.getOrderId(),
                DeliveryAssignedEvent.builder()
                        .courierName(assignedDelivery.getCourierName())
                        .orderId(assignedDelivery.getOrderId())
                        .etaMinutes(assignedDelivery.getEtaMinutes())
                        .build()
        ).thenAccept(kafkaDelivery -> {
            log.info("Delivery assigned to delivery={}", assignedDelivery.getId());
        });
    }
}
