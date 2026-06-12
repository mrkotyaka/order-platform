package ru.mrkotyaka.orderservice.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.orderservice.domain.OrderProcessor;

@Slf4j
@EnableKafka
@AllArgsConstructor
@Configuration
public class DeliveryAssignedKafkaConsumer {

    private final OrderProcessor orderProcessor;

    @KafkaListener(
            topics = "${delivery-assigned-topic}",
            containerFactory = "deliveryAssignedEventEventListenerFactory"
    )
    public void listen(DeliveryAssignedEvent event) {
        log.info("Received delivery assigned event orderId `{}`", event.orderId());
        if (event.canceledAt() == null) {
            orderProcessor.processDeliveryAssigned(event);
        }
    }
}
