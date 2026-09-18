package com.payment.ingestor.event;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountCreatedEvent(
        UUID eventId,
        String accountId,
        UUID userId,
        String accountName,
        AccountType accountType,
        BigDecimal accountBalance,
        AccountStatus status,
        String currency,
        LocalDate openedDate,
        Long sourceVersion
) {
}

