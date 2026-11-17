package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.OUTBOX_FOR_USERS_CREATED;

@Getter
@Setter
@ConfigurationProperties(prefix = OUTBOX_FOR_USERS_CREATED)
public class UserCreatedOutboxProperties {

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
