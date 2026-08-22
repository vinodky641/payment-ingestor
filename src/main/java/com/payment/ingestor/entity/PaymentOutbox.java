package com.payment.ingestor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Entity
@Table(name = PAYMENT_OUTBOX_TABLE_NAME)
public class PaymentOutbox {
    @Id
    private UUID eventId;

    @Column(nullable = false, length = 64)
    private String aggregateId;

    @Column(nullable = false, length = 120)
    private String eventType;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 20)
    private String status;

    protected PaymentOutbox() {
    }

    public PaymentOutbox(UUID eventId, String aggregateId, String eventType, String payload) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.status = PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public void markPublished() {
        status = PUBLISH_PAYMENT_TO_KAFKA_STATUS_PUBLISHED;
    }
}

