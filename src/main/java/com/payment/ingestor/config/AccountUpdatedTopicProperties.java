package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.KAFKA_TOPIC_FOR_ACCOUNTS_UPDATED;

@Getter
@Setter
@ConfigurationProperties(prefix = KAFKA_TOPIC_FOR_ACCOUNTS_UPDATED)
public class AccountUpdatedTopicProperties {

    private String name;

    private int partitions;

    private short replicationFactor;

    private String retentionMs;

}