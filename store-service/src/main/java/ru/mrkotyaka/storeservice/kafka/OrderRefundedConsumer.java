package ru.mrkotyaka.storeservice.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;
import ru.mrkotyaka.storeservice.domain.StoreProcessor;

@Slf4j
@EnableKafka
@Configuration
@AllArgsConstructor
public class OrderRefundedConsumer {
    private final StoreProcessor storeProcessor;

    @KafkaListener(
            topics = "${warehouse-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderRsDtoListenerFactory")
    public void listen(OrderRsDto event) {
        log.info("KafkaListener: Received refunded stocks event {}", event);
        storeProcessor.warehouseRefundedProcess(event);
    }

}
