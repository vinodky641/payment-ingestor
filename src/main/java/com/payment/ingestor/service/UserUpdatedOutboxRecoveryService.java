package com.payment.ingestor.service;

import com.payment.ingestor.config.UserUpdatedOutboxProperties;
import com.payment.ingestor.repository.UserUpdatedOutboxRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdatedOutboxRecoveryService {

    private final UserUpdatedOutboxRepository repository;
    private final UserUpdatedOutboxProperties properties;
    private final MeterRegistry meterRegistry;

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_STALE_USER_UPDATED_RECOVERY)
    @Transactional
    public void recoverStaleEvents() {

        Instant staleBefore = Instant.now().minus(
                properties.getRecovery().getStaleAfterMinutes(),
                ChronoUnit.MINUTES
        );
        int recovered = repository.resetStaleEvents(staleBefore);
        if (recovered > 0) {
            log.warn("Recovered {} stale UserUpdatedOutbox events. staleBefore={}",
                    recovered,
                    staleBefore
            );

            meterRegistry.counter(
                    USER_UPDATED_OUTBOX_EVENTS_METRIC_RECOVERED,
                    METRICS_TYPE,
                    EVENT_TYPE_USER_UPDATED
            ).increment(recovered);
        }
    }

}
