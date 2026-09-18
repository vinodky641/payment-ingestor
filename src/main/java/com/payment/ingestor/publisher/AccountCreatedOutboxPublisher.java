package com.payment.ingestor.publisher;

import com.payment.ingestor.config.AccountCreatedOutboxProperties;
import com.payment.ingestor.config.AccountCreatedTopicProperties;
import com.payment.ingestor.entity.AccountCreatedOutbox;
import com.payment.ingestor.event.AccountCreatedEvent;
import com.payment.ingestor.service.AccountCreatedOutboxClaimService;
import com.payment.ingestor.service.AccountCreatedOutboxStateService;
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
public class AccountCreatedOutboxPublisher {

    private final AccountCreatedOutboxClaimService claimService;
    private final AccountCreatedOutboxStateService stateService;
    private final KafkaTemplate<String, AccountCreatedEvent> kafkaTemplate;
    private final ObjectMapper mapper;
    private final AccountCreatedOutboxProperties properties;
    private final AccountCreatedTopicProperties accountCreatedTopicProperties;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private Timer publishTimer;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder(
                        ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_INFLIGHT,
                        inFlight,
                        AtomicInteger::get
                )
                .description("Number of AccountCreated outbox events currently waiting for Kafka acknowledgement")
                .tag(METRICS_TYPE, EVENT_TYPE_ACCOUNT_CREATED)
                .register(meterRegistry);

        publishTimer = Timer.builder(
                        ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY
                )
                .description("Kafka publish latency for AccountCreated outbox events")
                .tag(METRICS_TYPE, EVENT_TYPE_ACCOUNT_CREATED)
                .register(meterRegistry);

    }

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_PENDING_ACCOUNT_CREATED_PUBLISH)
    public void publishPendingEvents() {

        int currentInFlight = inFlight.get();
        int availableCapacity = properties.getMaxInFlight() - currentInFlight;
        if (availableCapacity <= 0) {
            log.debug(
                    "AccountCreated publisher reached max in-flight capacity. inFlight={}, maxInFlight={}",
                    currentInFlight,
                    properties.getMaxInFlight()
            );
            return;
        }

        int batchSize = Math.min(properties.getBatchSize(), availableCapacity);
        List<AccountCreatedOutbox> accountCreatedOutboxList = claimService.claimBatch(batchSize);
        if (accountCreatedOutboxList.isEmpty()) {
            return;
        }
        meterRegistry.counter(
                ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_CLAIMED,
                METRICS_TYPE,
                EVENT_TYPE_ACCOUNT_CREATED
        ).increment(accountCreatedOutboxList.size());

        for (AccountCreatedOutbox accountCreatedOutbox : accountCreatedOutboxList) {
            publish(accountCreatedOutbox);
        }
    }

    private void publish(AccountCreatedOutbox accountCreatedOutbox) {

        final AccountCreatedEvent accountCreatedEvent;
        try {
            accountCreatedEvent = mapper.readValue(accountCreatedOutbox.getPayload(), AccountCreatedEvent.class);
        } catch (JacksonException ex) {
            log.error(
                    "Failed to deserialize AccountCreatedEvent. eventId={}, accountId={}",
                    accountCreatedOutbox.getEventId(),
                    accountCreatedOutbox.getAccountId(),
                    ex
            );
            stateService.resetToPending(accountCreatedOutbox.getEventId());
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        inFlight.incrementAndGet();
        try {
            kafkaTemplate.send(
                    accountCreatedTopicProperties.getName(),
                    accountCreatedEvent.accountId(),
                    accountCreatedEvent
            ).whenComplete(
                    (result, ex) -> handleResult(accountCreatedOutbox, timer, result, ex)
            );
        } catch (Exception ex) {
            log.error(
                    "Failure while submitting AccountCreatedEvent to Kafka. eventId={}, accountId={}",
                    accountCreatedOutbox.getEventId(),
                    accountCreatedOutbox.getAccountId(),
                    ex
            );
            stateService.resetToPending(accountCreatedOutbox.getEventId());
            inFlight.decrementAndGet();
            timer.stop(publishTimer);
        }
    }

    private void handleResult(
            AccountCreatedOutbox accountCreatedOutbox,
            Timer.Sample timer,
            SendResult<String, AccountCreatedEvent> result,
            Throwable ex
    ) {
        try {
            if (ex == null) {
                log.debug(
                        "AccountCreatedEvent published successfully. eventId={}, accountId={}, topic={}, partition={}, offset={}",
                        accountCreatedOutbox.getEventId(),
                        accountCreatedOutbox.getAccountId(),
                        accountCreatedTopicProperties.getName(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                stateService.markPublished(accountCreatedOutbox.getEventId());
                meterRegistry.counter(
                        ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_PUBLISHED,
                        METRICS_TYPE,
                        EVENT_TYPE_ACCOUNT_CREATED
                ).increment();
            } else {
                log.error("Failed to publish AccountCreatedEvent. eventId={}, accountId={}",
                        accountCreatedOutbox.getEventId(),
                        accountCreatedOutbox.getAccountId(),
                        ex
                );
                stateService.resetToPending(accountCreatedOutbox.getEventId());
                meterRegistry.counter(
                        ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_FAILED,
                        METRICS_TYPE,
                        EVENT_TYPE_ACCOUNT_CREATED
                ).increment();
            }

        } finally {

            timer.stop(publishTimer);
            inFlight.decrementAndGet();
        }
    }

}
