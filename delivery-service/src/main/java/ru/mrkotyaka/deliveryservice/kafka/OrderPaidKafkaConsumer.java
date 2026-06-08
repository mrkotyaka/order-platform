package ru.mrkotyaka.deliveryservice.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.deliveryservice.domain.DeliveryProcessor;

@Slf4j
@EnableKafka
@AllArgsConstructor
@Configuration
public class OrderPaidKafkaConsumer {

    private final DeliveryProcessor deliveryProcessor;

    @KafkaListener(
            topics = "${order-paid-topic}",
            containerFactory = "orderPaidEventListenerFactory"
    )
    public void listen(OrderPaidEvent event) {
        log.info("Received order paid event {}", event);
        deliveryProcessor.processOrderPaid(event);

    }
}
