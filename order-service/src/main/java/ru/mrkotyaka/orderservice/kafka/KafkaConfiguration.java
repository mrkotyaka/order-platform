package ru.mrkotyaka.orderservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.mrkotyaka.commonlibs.kafka.delivery.DeliveryAssignedEvent;
import ru.mrkotyaka.commonlibs.kafka.delivery.OrderPaidEvent;
import ru.mrkotyaka.commonlibs.kafka.notification.NotificationEvent;

import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfiguration {

    // 1. Общая фабрика для продюсеров
    @Bean
    public DefaultKafkaProducerFactory<UUID, Object> producerFactory(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties(null);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    // 2. Шаблон для OrderPaidEvent
    @Bean
    public KafkaTemplate<UUID, OrderPaidEvent> kafkaTemplate(DefaultKafkaProducerFactory<UUID, Object> pf) {
        return new KafkaTemplate(pf);
    }

    // 3. Шаблон для NotificationEvent
    @Bean
    public KafkaTemplate<UUID, NotificationEvent> notificationKafkaTemplate(DefaultKafkaProducerFactory<UUID, Object> pf) {
        return new KafkaTemplate(pf);
    }

    @Bean
    public ConsumerFactory<UUID, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.mrkotyaka.commonlibs.*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public KafkaListenerContainerFactory<?> deliveryAssignedEventEventListenerFactory(
            ConsumerFactory<UUID, DeliveryAssignedEvent> deliveryAssignedEventConsumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<UUID, DeliveryAssignedEvent>();
        factory.setConsumerFactory(deliveryAssignedEventConsumerFactory);
        factory.setBatchListener(false);

        // Перенес использование виртуальных потоков сюда
        factory.getContainerProperties().setListenerTaskExecutor(new VirtualThreadTaskExecutor());

        return factory;
    }
}
