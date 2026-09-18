package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.outbox.account-updated")
public class AccountUpdatedOutboxProperties {

    private int batchSize = 50;
    private int maxInFlight = 200;
    private Recovery recovery = new Recovery();

    @Getter
    @Setter
    public static class Recovery {

        private long staleAfterMinutes = 5;
        private long fixedDelayMs = 60_000;
    }

}