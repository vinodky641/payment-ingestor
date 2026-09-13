package com.payment.ingestor.publisher;


import com.payment.ingestor.config.PaymentOutboxProperties;
import com.payment.ingestor.entity.PaymentOutbox;
import com.payment.ingestor.event.PaymentEvent;
import com.payment.ingestor.service.PaymentOutboxClaimService;
import com.payment.ingestor.service.PaymentOutboxStateService;
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
public class PaymentOutboxPublisher {

    private final PaymentOutboxClaimService claimService;
    private final PaymentOutboxStateService stateService;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final ObjectMapper mapper;
    private final PaymentOutboxProperties properties;
    private final MeterRegistry meterRegistry;
    private final AtomicInteger inFlight = new AtomicInteger(0);

    private Timer publishTimer;

    @PostConstruct
    public void registerMetrics() {

        Gauge.builder(
                        PAYMENT_OUTBOX_EVENTS_METRIC_INFLIGHT,
                        inFlight,
                        AtomicInteger::get
                )
                .description("Number of payment outbox events currently waiting for Kafka acknowledgement")
                .tag(METRICS_TYPE, EVENT_TYPE_PAYMENT)
                .register(meterRegistry);

        publishTimer = Timer.builder(
                        PAYMENT_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY
                )
                .description("Kafka publish latency for payment outbox events")
                .tag(METRICS_TYPE, EVENT_TYPE_PAYMENT)
                .register(meterRegistry);

    }


    @Scheduled(fixedDelayString = TIME_INTERVAL_FOR_RUNNING_PENDING_PAYMENT_PUBLISH)
    public void publishPendingEvents() {

        int currentInFlight = inFlight.get();
        int availableCapacity = properties.getMaxInFlight() - currentInFlight;
        if (availableCapacity <= 0) {
            log.debug(
                    "PaymentOutbox publisher reached max in-flight capacity. inFlight={}, maxInFlight={}",
                    currentInFlight,
                    properties.getMaxInFlight()
            );
            return;
        }

        int batchSize = Math.min(properties.getBatchSize(), availableCapacity);
        List<PaymentOutbox> paymentOutboxList = claimService.claimBatch(batchSize);
        if (paymentOutboxList.isEmpty()) {
            return;
        }
        meterRegistry.counter(
                PAYMENT_OUTBOX_EVENTS_METRIC_CLAIMED,
                METRICS_TYPE,
                EVENT_TYPE_PAYMENT
        ).increment(paymentOutboxList.size());

        for (PaymentOutbox paymentOutbox : paymentOutboxList) {
            publish(paymentOutbox);
        }
    }

    private void publish(PaymentOutbox paymentOutbox) {

        final PaymentEvent paymentEvent;
        try {
            paymentEvent = mapper.readValue(paymentOutbox.getPayload(), PaymentEvent.class);
        } catch (JacksonException ex) {
            log.error(
                    "Failed to deserialize PaymentEvent. eventId={}, paymentId={}",
                    paymentOutbox.getEventId(),
                    paymentOutbox.getPaymentId(),
                    ex
            );
            stateService.resetToPending(paymentOutbox.getEventId());
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        inFlight.incrementAndGet();
        try {
            kafkaTemplate.send(
                    KAFKA_TOPIC_NAME_FOR_PAYMENTS_SUBMITTED,
                    paymentEvent.paymentId(),
                    paymentEvent
            ).whenComplete(
                    (result, ex) -> handleResult(paymentOutbox, timer, result, ex)
            );
        } catch (Exception ex) {
            log.error(
                    "Failure while submitting PaymentEvent to Kafka. eventId={}, paymentId={}",
                    paymentOutbox.getEventId(),
                    paymentOutbox.getPaymentId(),
                    ex
            );
            stateService.resetToPending(paymentOutbox.getEventId());
            inFlight.decrementAndGet();
            timer.stop(publishTimer);
        }
    }

    private void handleResult(
            PaymentOutbox paymentOutbox,
            Timer.Sample timer,
            SendResult<String, PaymentEvent> result,
            Throwable ex
    ) {
        try {
            if (ex == null) {
                log.debug(
                        "PaymentEvent published successfully. eventId={}, paymentId={}, topic={}, partition={}, offset={}",
                        paymentOutbox.getEventId(),
                        paymentOutbox.getPaymentId(),
                        KAFKA_TOPIC_NAME_FOR_PAYMENTS_SUBMITTED,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
                stateService.markPublished(paymentOutbox.getEventId());
                meterRegistry.counter(
                        PAYMENT_OUTBOX_EVENTS_METRIC_PUBLISHED,
                        METRICS_TYPE,
                        EVENT_TYPE_PAYMENT
                ).increment();
            } else {
                log.error("Failed to publish PaymentEvent. eventId={}, paymentId={}",
                        paymentOutbox.getEventId(),
                        paymentOutbox.getPaymentId(),
                        ex
                );
                stateService.resetToPending(paymentOutbox.getEventId());
                meterRegistry.counter(
                        PAYMENT_OUTBOX_EVENTS_METRIC_FAILED,
                        METRICS_TYPE,
                        EVENT_TYPE_PAYMENT
                ).increment();
            }

        } finally {

            timer.stop(publishTimer);
            inFlight.decrementAndGet();
        }

    }

}
