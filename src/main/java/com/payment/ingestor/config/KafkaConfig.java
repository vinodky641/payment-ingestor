package com.payment.ingestor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic submitted() {
        return TopicBuilder
                .name(KAFKA_TOPIC_NAME)
                .partitions(KAFKA_TOPIC_PARTITIONS)
                .replicas(KAFKA_TOPIC_REPLICAS)
                .build();
    }
}
