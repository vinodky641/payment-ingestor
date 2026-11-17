package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.KAFKA_PRODUCER;

@Getter
@Setter
@ConfigurationProperties(prefix = KAFKA_PRODUCER)
public class KafkaProducerProperties {

    private String acks;

    private boolean enableIdempotence;

    private int retries;

    private int maxInFlightRequestsPerConnection;

    private String compressionType;

    private int lingerMs;

    private int batchSize;

    private int requestTimeoutMs;

    private int deliveryTimeoutMs;

    private boolean addTypeInfoHeaders;
    
}
