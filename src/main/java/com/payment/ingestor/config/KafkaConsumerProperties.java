package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.KAFKA_CONSUMER;

@Getter
@Setter
@ConfigurationProperties(prefix = KAFKA_CONSUMER)
public class KafkaConsumerProperties {

    private String balanceGroupId;

    private String autoOffsetReset = "earliest";

    private boolean enableAutoCommit = false;

    private int maxPollRecords = 100;

}
