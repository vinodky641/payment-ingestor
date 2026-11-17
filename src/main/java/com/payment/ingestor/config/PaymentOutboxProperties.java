package com.payment.ingestor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.payment.ingestor.constant.PaymentIngestorConstants.OUTBOX_FOR_PAYMENTS;

@Getter
@Setter
@ConfigurationProperties(prefix = OUTBOX_FOR_PAYMENTS)
public class PaymentOutboxProperties {

    private int batchSize = 100;
    private int maxInFlight = 1000;
    private Recovery recovery = new Recovery();

    @Getter
    @Setter
    public static class Recovery {

        private long staleAfterMinutes = 5;
        private long fixedDelayMs = 60_000;
    }

}
