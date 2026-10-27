package com.payment.ingestor.config;

import com.payment.ingestor.event.AccountBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaRetryTopic;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.payment.ingestor.constant.PaymentIngestorConstants.TRUSTED_PACKAGE_OF_PAYMENT_EVENT;

@Configuration
@RequiredArgsConstructor
@EnableKafkaRetryTopic
@EnableConfigurationProperties({
        KafkaConsumerProperties.class,
        AccountBalanceChangedRetryTopicProperties.class,
        RetryTopicProducerProperties.class
})
public class AccountBalanceChangedKafkaConsumerConfig {

    private final KafkaConsumerProperties properties;
    private final AccountBalanceChangedRetryTopicProperties retryProperties;
    private final RetryTopicProducerProperties producerProperties;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, AccountBalanceChangedEvent> accountBalanceChangedConsumerFactory() {

        Map<String, Object> consumerProperties = new HashMap<>();

        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getBalanceGroupId());
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.isEnableAutoCommit());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, properties.getMaxPollRecords());

        JacksonJsonDeserializer<AccountBalanceChangedEvent> valueDeserializer = new JacksonJsonDeserializer<>(
                AccountBalanceChangedEvent.class,
                false
        );

        valueDeserializer.addTrustedPackages(TRUSTED_PACKAGE_OF_PAYMENT_EVENT);

        return new DefaultKafkaConsumerFactory<>(
                consumerProperties,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountBalanceChangedEvent> accountBalanceChangedKafkaListenerContainerFactory(
            ConsumerFactory<String, AccountBalanceChangedEvent> accountBalanceChangedConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, AccountBalanceChangedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountBalanceChangedConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    // Define a strongly-typed ProducerFactory that serializes AccountBalanceChangedEvent into JSON
    // This is required because non-blocking retries act as a producer to forward failed messages
    @Bean
    public ProducerFactory<String, AccountBalanceChangedEvent> accountBalanceChangedProducerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, producerProperties.getAcks());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerProperties.isEnableIdempotence());
        properties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, producerProperties.getMaxInFlightRequestsPerConnection());
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producerProperties.getDeliveryTimeoutMs());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producerProperties.getRequestTimeoutMs());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, producerProperties.getLingerMs());
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producerProperties.getCompressionType());

        return new DefaultKafkaProducerFactory<>(properties);
    }

    // Define the strongly-typed KafkaTemplate
    @Bean
    public KafkaTemplate<String, AccountBalanceChangedEvent> accountBalanceChangedKafkaTemplate(

            ProducerFactory<String, AccountBalanceChangedEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Configure the RetryTopicConfiguration using your custom Template and Factory
    @Bean
    public RetryTopicConfiguration accountBalanceChangedRetryTopic(

            KafkaTemplate<String, AccountBalanceChangedEvent> accountBalanceChangedKafkaTemplate,
            ConcurrentKafkaListenerContainerFactory<String, AccountBalanceChangedEvent> accountBalanceChangedKafkaListenerContainerFactory
    ) {
        return RetryTopicConfigurationBuilder
                .newInstance()
                .includeTopics(List.of(retryProperties.getTopicName()))
                .exponentialBackoff(retryProperties.getInitialDelayMs(), retryProperties.getMultiplier(), retryProperties.getMaxDelayMs())
                .maxAttempts(retryProperties.getMaxAttempts())
                .dltSuffix(retryProperties.getDltSuffix())
                .autoCreateTopicsWith(retryProperties.getNumPartitions(), retryProperties.getReplicationFactor())
                .listenerFactory(accountBalanceChangedKafkaListenerContainerFactory)
                .retryOn(List.of())
                .create(accountBalanceChangedKafkaTemplate);
    }

}

