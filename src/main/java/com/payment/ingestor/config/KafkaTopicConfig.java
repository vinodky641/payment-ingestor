package com.payment.ingestor.config;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.payment.ingestor.constant.PaymentIngestorConstants.RETENTION_MS;

@Configuration
@AllArgsConstructor
public class KafkaTopicConfig {

    private final UserCreatedTopicProperties userCreatedTopicProperties;
    private final UserUpdatedTopicProperties userUpdatedTopicProperties;
    private final AccountCreatedTopicProperties accountCreatedTopicProperties;
    private final AccountUpdatedTopicProperties accountUpdatedTopicProperties;
    private final PaymentSubmittedTopicProperties paymentSubmittedTopicProperties;

    @Bean
    public NewTopic usersCreatedTopic() {
        return TopicBuilder
                .name(userCreatedTopicProperties.getName())
                .partitions(userCreatedTopicProperties.getPartitions())
                .replicas(userCreatedTopicProperties.getReplicationFactor())
                .config(RETENTION_MS, userCreatedTopicProperties.getRetentionMs())
                .build();
    }

    @Bean
    public NewTopic usersUpdatedTopic() {
        return TopicBuilder
                .name(userUpdatedTopicProperties.getName())
                .partitions(userUpdatedTopicProperties.getPartitions())
                .replicas(userUpdatedTopicProperties.getReplicationFactor())
                .config(RETENTION_MS, userUpdatedTopicProperties.getRetentionMs())
                .build();
    }

    @Bean
    public NewTopic accountsCreatedTopic() {
        return TopicBuilder
                .name(accountCreatedTopicProperties.getName())
                .partitions(accountCreatedTopicProperties.getPartitions())
                .replicas(accountCreatedTopicProperties.getReplicationFactor())
                .config(RETENTION_MS, accountCreatedTopicProperties.getRetentionMs())
                .build();
    }

    @Bean
    public NewTopic accountsUpdatedTopic() {
        return TopicBuilder
                .name(accountUpdatedTopicProperties.getName())
                .partitions(accountUpdatedTopicProperties.getPartitions())
                .replicas(accountUpdatedTopicProperties.getReplicationFactor())
                .config(RETENTION_MS, accountUpdatedTopicProperties.getRetentionMs())
                .build();
    }

    @Bean
    NewTopic paymentsSubmittedTopic() {
        return TopicBuilder
                .name(paymentSubmittedTopicProperties.getName())
                .partitions(paymentSubmittedTopicProperties.getPartitions())
                .replicas(paymentSubmittedTopicProperties.getReplicationFactor())
                .config(RETENTION_MS, paymentSubmittedTopicProperties.getRetentionMs())
                .build();
    }

}