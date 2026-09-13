package com.payment.ingestor.entity;

import com.payment.ingestor.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.PAYMENT_OUTBOX_TABLE_NAME;


@Getter
@Entity
@Table(name = PAYMENT_OUTBOX_TABLE_NAME,
        indexes = {
                @Index(
                        name = "idx_payment_outbox_status_created_at",
                        columnList = "status, created_at"
                )
        }
)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @Column(name = "published_at")
    private Instant publishedAt;

    protected PaymentOutbox() {
    }

    public PaymentOutbox(UUID eventId, String paymentId, String eventType, String payload) {
        this.eventId = eventId;
        this.paymentId = paymentId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
        this.claimedAt = Instant.now();
        this.attemptCount++;
    }

}

