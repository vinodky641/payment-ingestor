package com.payment.ingestor.publisher;

import com.payment.ingestor.config.AccountUpdatedOutboxProperties;
import com.payment.ingestor.config.AccountUpdatedTopicProperties;
import com.payment.ingestor.entity.AccountUpdatedOutbox;
import com.payment.ingestor.event.AccountUpdatedEvent;
import com.payment.ingestor.service.AccountUpdatedOutboxClaimService;
import com.payment.ingestor.service.AccountUpdatedOutboxStateService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountUpdatedOutboxPublisher {

    private final AccountUpdatedOutboxClaimService claimService;
    private final AccountUpdatedOutboxStateService stateService;
    private final KafkaTemplate<String, AccountUpdatedEvent> kafkaTemplate;
    private final ObjectMapper mapper;
    private final AccountUpdatedOutboxProperties properties;
    private final AccountUpdatedTopicProperties accountUpdatedTopicProperties;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private Timer publishTimer;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder(
                        ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_INFLIGHT,
                        inFlight,
                        AtomicInteger::get
                )
                .description("Number of AccountUpdated outbox events currently waiting for Kafka acknowledgement")
                .tag(METRICS_TYPE, EVENT_TYPE_ACCOUNT_UPDATED)
                .register(meterRegistry);

        publishTimer = Timer.builder(
                        ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY
                )
                .description("Kafka publish latency for AccountUpdated outbox events")
                .tag(METRICS_TYPE, EVENT_TYPE_ACCOUNT_UPDATED)
                .register(meterRegistry);

    }

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_PENDING_ACCOUNT_UPDATED_PUBLISH)
    public void publishPendingEvents() {

        int currentInFlight = inFlight.get();
        int availableCapacity = properties.getMaxInFlight() - currentInFlight;
        if (availableCapacity <= 0) {
            log.debug(
                    "AccountUpdated publisher reached max in-flight capacity. inFlight={}, maxInFlight={}",
                    currentInFlight,
                    properties.getMaxInFlight()
            );
            return;
        }

        int batchSize = Math.min(properties.getBatchSize(), availableCapacity);
        List<AccountUpdatedOutbox> accountUpdatedOutboxList = claimService.claimBatch(batchSize);
        if (accountUpdatedOutboxList.isEmpty()) {
            return;
        }
        meterRegistry.counter(
                ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_CLAIMED,
                METRICS_TYPE,
                EVENT_TYPE_ACCOUNT_UPDATED
        ).increment(accountUpdatedOutboxList.size());

        for (AccountUpdatedOutbox accountUpdatedOutbox : accountUpdatedOutboxList) {
            publish(accountUpdatedOutbox);
        }
    }

    private void publish(AccountUpdatedOutbox accountUpdatedOutbox) {

        final AccountUpdatedEvent accountUpdatedEvent;
        try {
            accountUpdatedEvent = mapper.readValue(accountUpdatedOutbox.getPayload(), AccountUpdatedEvent.class);
        } catch (JacksonException ex) {
            log.error(
                    "Failed to deserialize AccountUpdatedEvent. eventId={}, accountId={}",
                    accountUpdatedOutbox.getEventId(),
                    accountUpdatedOutbox.getAccountId(),
                    ex
            );
            stateService.resetToPending(accountUpdatedOutbox.getEventId());
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        inFlight.incrementAndGet();
        try {
            kafkaTemplate.send(
                    accountUpdatedTopicProperties.getName(),
                    accountUpdatedEvent.accountId(),
                    accountUpdatedEvent
            ).whenComplete(
                    (result, ex) -> handleResult(accountUpdatedOutbox, timer, result, ex)
            );
        } catch (Exception ex) {
            log.error(
                    "Failure while submitting AccountUpdatedEvent to Kafka. eventId={}, accountId={}",
                    accountUpdatedOutbox.getEventId(),
                    accountUpdatedOutbox.getAccountId(),
                    ex
            );
            stateService.resetToPending(accountUpdatedOutbox.getEventId());
            inFlight.decrementAndGet();
            timer.stop(publishTimer);
        }
    }

    private void handleResult(
            AccountUpdatedOutbox accountUpdatedOutbox,
            Timer.Sample timer,
            SendResult<String, AccountUpdatedEvent> result,
            Throwable ex
    ) {
        try {
            if (ex == null) {
                log.debug(
                        "AccountUpdatedEvent published successfully. eventId={}, accountId={}, topic={}, partition={}, offset={}",
                        accountUpdatedOutbox.getEventId(),
                        accountUpdatedOutbox.getAccountId(),
                        accountUpdatedTopicProperties.getName(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                stateService.markPublished(accountUpdatedOutbox.getEventId());
                meterRegistry.counter(
                        ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_PUBLISHED,
                        METRICS_TYPE,
                        EVENT_TYPE_ACCOUNT_UPDATED
                ).increment();
            } else {
                log.error("Failed to publish AccountUpdatedEvent. eventId={}, accountId={}",
                        accountUpdatedOutbox.getEventId(),
                        accountUpdatedOutbox.getAccountId(),
                        ex
                );
                stateService.resetToPending(accountUpdatedOutbox.getEventId());
                meterRegistry.counter(
                        ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_FAILED,
                        METRICS_TYPE,
                        EVENT_TYPE_ACCOUNT_UPDATED
                ).increment();
            }

        } finally {

            timer.stop(publishTimer);
            inFlight.decrementAndGet();
        }
    }

}