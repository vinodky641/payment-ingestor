package com.payment.ingestor.entity;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.payment.ingestor.constant.PaymentIngestorConstants.ACCOUNTS_TABLE_NAME;

@Entity
@Table(name = ACCOUNTS_TABLE_NAME)
public class Account {
    @Id
    @Column(name = "account_id", length = 64)
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

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }
}

