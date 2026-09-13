package com.payment.ingestor.entity;

import com.payment.ingestor.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.USERS_UPDATED_OUTBOX_TABLE_NAME;

@Entity
@Table(
        name = USERS_UPDATED_OUTBOX_TABLE_NAME,
        indexes = {
                @Index(
                        name = "idx_users_updated_outbox_status_created_at",
                        columnList = "status, created_at"
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdatedOutbox {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "claimed_at")
    private Instant claimedAt;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private int attemptCount = 0;

    @Column(name = "published_at")
    private Instant publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
        this.claimedAt = Instant.now();
        this.attemptCount++;
    }

}