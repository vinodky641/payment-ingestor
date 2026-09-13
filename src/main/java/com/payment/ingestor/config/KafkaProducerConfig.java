package com.payment.ingestor.config;

import com.payment.ingestor.event.PaymentEvent;
import com.payment.ingestor.event.UserCreatedEvent;
import com.payment.ingestor.event.UserUpdatedEvent;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

    private final KafkaProducerProperties producerProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> producerProperties() {

        Map<String, Object> properties = new HashMap<>();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, producerProperties.getAcks());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerProperties.isEnableIdempotence());
        properties.put(ProducerConfig.RETRIES_CONFIG, producerProperties.getRetries());
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, producerProperties.getMaxInFlightRequestsPerConnection());
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producerProperties.getCompressionType());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, producerProperties.getLingerMs());
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperties.getBatchSize());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producerProperties.getRequestTimeoutMs());
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producerProperties.getDeliveryTimeoutMs());
        properties.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, producerProperties.isAddTypeInfoHeaders());

        return properties;
    }

    // For User Created events
    @Bean
    public ProducerFactory<String, UserCreatedEvent> userCreatedProducerFactory() {

        return new DefaultKafkaProducerFactory<>(
                producerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, UserCreatedEvent> userCreatedKafkaTemplate(

            @Qualifier("userCreatedProducerFactory")
            ProducerFactory<String, UserCreatedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    // For User Updated events
    @Bean
    public ProducerFactory<String, UserUpdatedEvent> userUpdatedProducerFactory() {

        return new DefaultKafkaProducerFactory<>(
                producerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, UserUpdatedEvent> userUpdatedKafkaTemplate(

            @Qualifier("userUpdatedProducerFactory")
            ProducerFactory<String, UserUpdatedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    // For Payment events
    @Bean
    public ProducerFactory<String, PaymentEvent> paymentProducerFactory() {

        return new DefaultKafkaProducerFactory<>(
                producerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, PaymentEvent> paymentKafkaTemplate(

            @Qualifier("paymentProducerFactory")
            ProducerFactory<String, PaymentEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}