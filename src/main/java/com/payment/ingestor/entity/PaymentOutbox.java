package com.payment.ingestor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Getter
@Entity
@Table(name = PAYMENT_OUTBOX_TABLE_NAME)
public class PaymentOutbox {
    @Id
    private UUID eventId;

    @Column(updatable = false, nullable = false, unique = true, length = 64)
    private String paymentId;

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

    public PaymentOutbox(UUID eventId, String paymentId, String eventType, String payload) {
        this.eventId = eventId;
        this.paymentId = paymentId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.status = PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING;
    }

    public void markPublished() {
        status = PUBLISH_PAYMENT_TO_KAFKA_STATUS_PUBLISHED;
    }
    
}

