package com.payment.ingestor.publisher;

import com.payment.ingestor.config.UserUpdatedOutboxProperties;
import com.payment.ingestor.config.UserUpdatedTopicProperties;
import com.payment.ingestor.entity.UserUpdatedOutbox;
import com.payment.ingestor.event.UserUpdatedEvent;
import com.payment.ingestor.service.UserUpdatedOutboxClaimService;
import com.payment.ingestor.service.UserUpdatedOutboxStateService;
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
public class UserUpdatedOutboxPublisher {

    private final UserUpdatedOutboxClaimService claimService;
    private final UserUpdatedOutboxStateService stateService;
    private final KafkaTemplate<String, UserUpdatedEvent> kafkaTemplate;
    private final ObjectMapper mapper;
    private final UserUpdatedOutboxProperties properties;
    private final UserUpdatedTopicProperties userUpdatedTopicProperties;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private Timer publishTimer;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder(
                        USER_UPDATED_OUTBOX_EVENTS_METRIC_INFLIGHT,
                        inFlight,
                        AtomicInteger::get
                )
                .description("Number of UserUpdated outbox events currently waiting for Kafka acknowledgement")
                .tag(METRICS_TYPE, EVENT_TYPE_USER_UPDATED)
                .register(meterRegistry);

        publishTimer = Timer.builder(
                        USER_UPDATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY
                )
                .description("Kafka publish latency for UserUpdated outbox events")
                .tag(METRICS_TYPE, EVENT_TYPE_USER_UPDATED)
                .register(meterRegistry);

    }

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_PENDING_USER_UPDATED_PUBLISH)
    public void publishPendingEvents() {

        int currentInFlight = inFlight.get();
        int availableCapacity = properties.getMaxInFlight() - currentInFlight;
        if (availableCapacity <= 0) {
            log.debug(
                    "UserUpdated publisher reached max in-flight capacity. inFlight={}, maxInFlight={}",
                    currentInFlight,
                    properties.getMaxInFlight()
            );
            return;
        }

        int batchSize = Math.min(properties.getBatchSize(), availableCapacity);
        List<UserUpdatedOutbox> userUpdatedOutboxList = claimService.claimBatch(batchSize);
        if (userUpdatedOutboxList.isEmpty()) {
            return;
        }
        meterRegistry.counter(
                USER_UPDATED_OUTBOX_EVENTS_METRIC_CLAIMED,
                METRICS_TYPE,
                EVENT_TYPE_USER_UPDATED
        ).increment(userUpdatedOutboxList.size());

        for (UserUpdatedOutbox userUpdatedOutbox : userUpdatedOutboxList) {
            publish(userUpdatedOutbox);
        }
    }

    private void publish(UserUpdatedOutbox userUpdatedOutbox) {

        final UserUpdatedEvent userUpdatedEvent;
        try {
            userUpdatedEvent = mapper.readValue(userUpdatedOutbox.getPayload(), UserUpdatedEvent.class);
        } catch (JacksonException ex) {
            log.error(
                    "Failed to deserialize UserUpdatedEvent. eventId={}, userId={}",
                    userUpdatedOutbox.getEventId(),
                    userUpdatedOutbox.getUserId(),
                    ex
            );
            stateService.resetToPending(userUpdatedOutbox.getEventId());
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        inFlight.incrementAndGet();
        try {
            kafkaTemplate.send(
                    userUpdatedTopicProperties.getName(),
                    userUpdatedEvent.userId().toString(),
                    userUpdatedEvent
            ).whenComplete(
                    (result, ex) -> handleResult(userUpdatedOutbox, timer, result, ex)
            );
        } catch (Exception ex) {
            log.error(
                    "Failure while submitting UserUpdatedEvent to Kafka. eventId={}, userId={}",
                    userUpdatedOutbox.getEventId(),
                    userUpdatedOutbox.getUserId(),
                    ex
            );
            stateService.resetToPending(userUpdatedOutbox.getEventId());
            inFlight.decrementAndGet();
            timer.stop(publishTimer);
        }
    }

    private void handleResult(
            UserUpdatedOutbox userUpdatedOutbox,
            Timer.Sample timer,
            SendResult<String, UserUpdatedEvent> result,
            Throwable ex
    ) {
        try {
            if (ex == null) {
                log.debug(
                        "UserUpdatedEvent published successfully. eventId={}, userId={}, topic={}, partition={}, offset={}",
                        userUpdatedOutbox.getEventId(),
                        userUpdatedOutbox.getUserId(),
                        userUpdatedTopicProperties.getName(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                stateService.markPublished(userUpdatedOutbox.getEventId());
                meterRegistry.counter(
                        USER_UPDATED_OUTBOX_EVENTS_METRIC_PUBLISHED,
                        METRICS_TYPE,
                        EVENT_TYPE_USER_UPDATED
                ).increment();
            } else {
                log.error("Failed to publish UserUpdatedEvent. eventId={}, userId={}",
                        userUpdatedOutbox.getEventId(),
                        userUpdatedOutbox.getUserId(),
                        ex
                );
                stateService.resetToPending(userUpdatedOutbox.getEventId());
                meterRegistry.counter(
                        USER_UPDATED_OUTBOX_EVENTS_METRIC_FAILED,
                        METRICS_TYPE,
                        EVENT_TYPE_USER_UPDATED
                ).increment();
            }

        } finally {

            timer.stop(publishTimer);
            inFlight.decrementAndGet();
        }
    }

}
