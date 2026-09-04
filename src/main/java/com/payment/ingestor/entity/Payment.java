package com.payment.ingestor.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.PAYMENT_TABLE_NAME;

@Getter
@Entity
@Table(
        name = PAYMENT_TABLE_NAME,
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payments_payment_id",
                        columnNames = "payment_id"
                ),
                @UniqueConstraint(
                        name = "uk_payments_user_idempotency_key",
                        columnNames = {
                                "initiated_by",
                                "idempotency_key"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_payments_debit_account_id",
                        columnList = "debit_account_id"
                ),
                @Index(
                        name = "idx_payments_credit_account_id",
                        columnList = "credit_account_id"
                )
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_id", updatable = false, nullable = false, unique = true, length = 64)
    private String paymentId;

    @Column(name = "idempotency_key", updatable = false, nullable = false, length = 64)
    private String idempotencyKey;

    @Column(name = "debit_account_id", nullable = false, length = 64)
    String debitAccountId;

    @Column(name = "credit_account_id", nullable = false, length = 64)
    String creditAccountId;

    @Column(nullable = false, precision = 19, scale = 4)
    BigDecimal amount;

    @Column(nullable = false, length = 3)
    String currency;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_payments_user"))
    private User user;

    public Payment() {
    }

    public Payment(
            String paymentId,
            String idempotencyKey,
            String debitAccountId,
            String creditAccountId,
            BigDecimal amount,
            String currency,
            Instant receivedAt,
            User user) {

        this.paymentId = paymentId;
        this.idempotencyKey = idempotencyKey;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
        this.amount = amount;
        this.currency = currency;
        this.receivedAt = receivedAt;
        this.user = user;
    }
    
}
