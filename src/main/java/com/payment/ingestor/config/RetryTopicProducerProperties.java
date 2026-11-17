package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.KAFKA_RETRY_TOPICS_PRODUCER;

@Getter
@Setter
@ConfigurationProperties(prefix = KAFKA_RETRY_TOPICS_PRODUCER)
public class RetryTopicProducerProperties {

    private String acks = "all";

    private boolean enableIdempotence = true;

    private int maxInFlightRequestsPerConnection = 5;

    private int deliveryTimeoutMs = 120_000;

    private int requestTimeoutMs = 30_000;

    private int lingerMs = 5;

    private String compressionType = "lz4";

}
