package com.payment.ingestor.service;

import com.payment.ingestor.config.AccountCreatedOutboxProperties;
import com.payment.ingestor.repository.AccountCreatedOutboxRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCreatedOutboxRecoveryService {

    private final AccountCreatedOutboxRepository repository;
    private final AccountCreatedOutboxProperties properties;
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_STALE_ACCOUNT_CREATED_RECOVERY)
    @Transactional
    public void recoverStaleEvents() {

        Instant staleBefore = Instant.now().minus(
                properties.getRecovery().getStaleAfterMinutes(),
                ChronoUnit.MINUTES
        );
        int recovered = repository.resetStaleEvents(staleBefore);

        if (recovered > 0) {
            log.warn("Recovered {} stale AccountCreatedOutbox events. staleBefore={}",
                    recovered,
                    staleBefore
            );

            meterRegistry.counter(
                    ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_RECOVERED,
                    METRICS_TYPE,
                    EVENT_TYPE_ACCOUNT_CREATED
            ).increment(recovered);
        }
    }

}
