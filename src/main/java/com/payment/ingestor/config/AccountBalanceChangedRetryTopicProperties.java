package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.kafka.retry-topics.accounts-balance-changed")
public class AccountBalanceChangedRetryTopicProperties {

    private String topicName = "accounts.balance-changed";

    private long initialDelayMs = 1000L;

    private double multiplier = 2.0;

    private long maxDelayMs = 10000L;

    private int maxAttempts = 4;

    private String dltSuffix = ".DLT";

    private int numPartitions = 12;

    private short replicationFactor = 1;

}
