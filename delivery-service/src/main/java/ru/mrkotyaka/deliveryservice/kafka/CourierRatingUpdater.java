package ru.mrkotyaka.deliveryservice.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import ru.mrkotyaka.commonlibs.dto.review.ReviewRsDto;
import ru.mrkotyaka.deliveryservice.domain.CourierProcessor;

@Slf4j
@EnableKafka
@Configuration
@AllArgsConstructor
public class CourierRatingUpdater {

    private final CourierProcessor  courierProcessor;

    @KafkaListener(
            topics = "${review-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "reviewEventListenerFactory")
    public void listen(ReviewRsDto event) {
        log.info("Received courier rating event {}", event);
        courierProcessor.updateCourierRating(event);
    }
}
