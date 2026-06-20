package ru.mrkotyaka.storeservice.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.mrkotyaka.commonlibs.dto.order.OrderRsDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<UUID, OrderRsDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.mrkotyaka.commonlibs.*");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaListenerContainerFactory<?> orderRsDtoListenerFactory(ConsumerFactory<UUID, OrderRsDto> orderRsDtoConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<UUID, OrderRsDto>();
        factory.setConsumerFactory(orderRsDtoConsumerFactory);
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    public KafkaTemplate<UUID, OrderRsDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
