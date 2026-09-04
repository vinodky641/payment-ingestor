package com.payment.ingestor.entity;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.ACCOUNTS_TABLE_NAME;

@Getter
@Entity
@Table(
        name = ACCOUNTS_TABLE_NAME,
        indexes = {
                @Index(
                        name = "idx_accounts_account_id_user_id",
                        columnList = "account_id,user_id"
                )
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_id", updatable = false, nullable = false, unique = true, length = 64)
    private String accountId;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal accountBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private LocalDate openedDate;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_accounts_user"))
    private User user;

    public Account() {
    }

    public Account(
            String accountId,
            String accountName,
            AccountType accountType,
            BigDecimal accountBalance,
            AccountStatus status,
            String currency,
            LocalDate openedDate,
            User user) {

        this.accountId = accountId;
        this.accountName = accountName;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
        this.status = status;
        this.currency = currency;
        this.openedDate = openedDate;
        this.user = user;
    }

}

