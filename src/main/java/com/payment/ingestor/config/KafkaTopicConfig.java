package com.payment.ingestor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic usersCreatedTopic(

            @Value("${kafka.topics.users-created.name}")
            String topicName,

            @Value("${kafka.topics.users-created.partitions}")
            int partitions,

            @Value("${kafka.topics.users-created.replication-factor}")
            short replicationFactor,

            @Value("${kafka.topics.users-created.retention-ms}")
            String retentionMs
    ) {
        return TopicBuilder
                .name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", retentionMs)
                .build();
    }

    @Bean
    public NewTopic usersUpdatedTopic(

            @Value("${kafka.topics.users-updated.name}")
            String topicName,

            @Value("${kafka.topics.users-updated.partitions}")
            int partitions,

            @Value("${kafka.topics.users-updated.replication-factor}")
            short replicationFactor,

            @Value("${kafka.topics.users-updated.retention-ms}")
            String retentionMs
    ) {
        return TopicBuilder
                .name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", retentionMs)
                .build();
    }

    @Bean
    NewTopic paymentsSubmittedTopic(

            @Value("${kafka.topics.payments-submitted.name}")
            String topicName,

            @Value("${kafka.topics.payments-submitted.partitions}")
            int partitions,

            @Value("${kafka.topics.payments-submitted.replication-factor}")
            short replicationFactor,

            @Value("${kafka.topics.payments-submitted.retention-ms}")
            String retentionMs
    ) {
        return TopicBuilder
                .name(topicName)
                .partitions(partitions)
                .replicas(replicationFactor)
                .config("retention.ms", retentionMs)
                .build();
    }

}