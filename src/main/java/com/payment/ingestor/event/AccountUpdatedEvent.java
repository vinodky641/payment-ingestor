package com.payment.ingestor.event;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AccountUpdatedEvent(
        UUID eventId,
        String accountId,
        UUID userId,
        String accountName,
        AccountType accountType,
        AccountStatus status,
        String currency,
        LocalDate openedDate,
        Instant updatedAt,
        Long sourceVersion
) {
}
