package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.retry-topics.producer")
public class RetryTopicProducerProperties {

    private String acks = "all";

    private boolean enableIdempotence = true;

    private int maxInFlightRequestsPerConnection = 5;

    private int deliveryTimeoutMs = 120_000;

    private int requestTimeoutMs = 30_000;

    private int lingerMs = 5;

    private String compressionType = "lz4";

}
