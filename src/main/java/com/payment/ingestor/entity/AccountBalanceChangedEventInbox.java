package com.payment.ingestor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.ACCOUNTS_BALANCE_CHANGED_EVENT_INBOX_TABLE_NAME;

@Entity
@Table(
        name = ACCOUNTS_BALANCE_CHANGED_EVENT_INBOX_TABLE_NAME,
        indexes = {
                @Index(
                        name = "idx_abc_inbox_account_id",
                        columnList = "account_id"
                ),
                @Index(
                        name = "idx_abc_inbox_payment_id",
                        columnList = "payment_id"
                ),
                @Index(
                        name = "idx_abc_inbox_processed_at",
                        columnList = "processed_at"
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBalanceChangedEventInbox {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "previous_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal previousBalance;

    @Column(name = "new_balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal newBalance;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "payment_id", nullable = false, length = 50)
    private String paymentId;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "balance_version", nullable = false)
    private long balanceVersion;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

}
