package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topics.payments-submitted")
public class PaymentSubmittedTopicProperties {

    private String name;

    private int partitions;

    private short replicationFactor;

    private String retentionMs;

}