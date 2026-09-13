package com.payment.ingestor.publisher;

import com.payment.ingestor.config.UserCreatedOutboxProperties;
import com.payment.ingestor.entity.UserCreatedOutbox;
import com.payment.ingestor.event.UserCreatedEvent;
import com.payment.ingestor.service.UserCreatedOutboxClaimService;
import com.payment.ingestor.service.UserCreatedOutboxStateService;
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
public class UserCreatedOutboxPublisher {

    private final UserCreatedOutboxClaimService claimService;
    private final UserCreatedOutboxStateService stateService;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;
    private final ObjectMapper mapper;
    private final UserCreatedOutboxProperties properties;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private Timer publishTimer;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder(
                        USER_CREATED_OUTBOX_EVENTS_METRIC_INFLIGHT,
                        inFlight,
                        AtomicInteger::get
                )
                .description("Number of UserCreated outbox events currently waiting for Kafka acknowledgement")
                .tag(METRICS_TYPE, EVENT_TYPE_USER_CREATED)
                .register(meterRegistry);

        publishTimer = Timer.builder(
                        USER_CREATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY
                )
                .description("Kafka publish latency for UserCreated outbox events")
                .tag(METRICS_TYPE, EVENT_TYPE_USER_CREATED)
                .register(meterRegistry);

    }

    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_PENDING_USER_CREATED_PUBLISH)
    public void publishPendingEvents() {

        int currentInFlight = inFlight.get();
        int availableCapacity = properties.getMaxInFlight() - currentInFlight;
        if (availableCapacity <= 0) {
            log.debug(
                    "UserCreated publisher reached max in-flight capacity. inFlight={}, maxInFlight={}",
                    currentInFlight,
                    properties.getMaxInFlight()
            );
            return;
        }

        int batchSize = Math.min(properties.getBatchSize(), availableCapacity);
        List<UserCreatedOutbox> userCreatedOutboxList = claimService.claimBatch(batchSize);
        if (userCreatedOutboxList.isEmpty()) {
            return;
        }
        meterRegistry.counter(
                USER_CREATED_OUTBOX_EVENTS_METRIC_CLAIMED,
                METRICS_TYPE,
                EVENT_TYPE_USER_CREATED
        ).increment(userCreatedOutboxList.size());

        for (UserCreatedOutbox userCreatedOutbox : userCreatedOutboxList) {
            publish(userCreatedOutbox);
        }
    }

    private void publish(UserCreatedOutbox userCreatedOutbox) {

        final UserCreatedEvent userCreatedEvent;
        try {
            userCreatedEvent = mapper.readValue(userCreatedOutbox.getPayload(), UserCreatedEvent.class);
        } catch (JacksonException ex) {
            log.error(
                    "Failed to deserialize UserCreatedEvent. eventId={}, userId={}",
                    userCreatedOutbox.getEventId(),
                    userCreatedOutbox.getUserId(),
                    ex
            );
            stateService.resetToPending(userCreatedOutbox.getEventId());
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        inFlight.incrementAndGet();
        try {
            kafkaTemplate.send(
                    KAFKA_TOPIC_NAME_FOR_USERS_CREATED,
                    userCreatedEvent.userId().toString(),
                    userCreatedEvent
            ).whenComplete(
                    (result, ex) -> handleResult(userCreatedOutbox, timer, result, ex)
            );
        } catch (Exception ex) {
            log.error(
                    "Failure while submitting UserCreatedEvent to Kafka. eventId={}, userId={}",
                    userCreatedOutbox.getEventId(),
                    userCreatedOutbox.getUserId(),
                    ex
            );
            stateService.resetToPending(userCreatedOutbox.getEventId());
            inFlight.decrementAndGet();
            timer.stop(publishTimer);
        }
    }

    private void handleResult(
            UserCreatedOutbox userCreatedOutbox,
            Timer.Sample timer,
            SendResult<String, UserCreatedEvent> result,
            Throwable ex
    ) {
        try {
            if (ex == null) {
                log.debug(
                        "UserCreatedEvent published successfully. eventId={}, userId={}, topic={}, partition={}, offset={}",
                        userCreatedOutbox.getEventId(),
                        userCreatedOutbox.getUserId(),
                        KAFKA_TOPIC_NAME_FOR_USERS_CREATED,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                stateService.markPublished(userCreatedOutbox.getEventId());
                meterRegistry.counter(
                        USER_CREATED_OUTBOX_EVENTS_METRIC_PUBLISHED,
                        METRICS_TYPE,
                        EVENT_TYPE_USER_CREATED
                ).increment();
            } else {
                log.error("Failed to publish UserCreatedEvent. eventId={}, userId={}",
                        userCreatedOutbox.getEventId(),
                        userCreatedOutbox.getUserId(),
                        ex
                );
                stateService.resetToPending(userCreatedOutbox.getEventId());
                meterRegistry.counter(
                        USER_CREATED_OUTBOX_EVENTS_METRIC_FAILED,
                        METRICS_TYPE,
                        EVENT_TYPE_USER_CREATED
                ).increment();
            }

        } finally {

            timer.stop(publishTimer);
            inFlight.decrementAndGet();
        }
    }

}
