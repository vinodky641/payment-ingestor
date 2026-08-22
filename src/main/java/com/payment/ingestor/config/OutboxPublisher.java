package com.payment.ingestor.config;


import com.payment.ingestor.entity.PaymentOutbox;
import com.payment.ingestor.event.PaymentEvent;
import com.payment.ingestor.repository.PaymentOutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.payment.ingestor.constant.PaymentIngestorConstants.KAFKA_TOPIC_NAME;
import static com.payment.ingestor.constant.PaymentIngestorConstants.PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING;

@Component
public class OutboxPublisher {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final ObjectMapper mapper;

    public OutboxPublisher(
            PaymentOutboxRepository paymentOutboxRepository,
            KafkaTemplate<String, PaymentEvent> kafkaTemplate,
            ObjectMapper mapper) {

        this.paymentOutboxRepository = paymentOutboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-ms:250}")
    @Transactional
    public void publish() {

        List<PaymentOutbox> rows = paymentOutboxRepository
                .findTop100ByStatusOrderByCreatedAtAsc(PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING);

        if(!rows.isEmpty()){
            for (PaymentOutbox row :rows) {
                try {
                    PaymentEvent paymentEvent = mapper.readValue(row.getPayload(), PaymentEvent.class);
                    kafkaTemplate.send(KAFKA_TOPIC_NAME, paymentEvent.paymentId(), paymentEvent).get();
                    row.markPublished();
                    paymentOutboxRepository.save(row);
                } catch (Exception ignored) {
                    break;
                }
            }
        }
    }
}

