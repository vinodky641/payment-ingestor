package com.payment.ingestor.event;

import com.payment.ingestor.enums.AccountStatus;
import com.payment.ingestor.enums.AccountType;

import java.time.Instant;
import java.util.UUID;

public record AccountUpdatedEvent(
        UUID eventId,
        String accountId,
        UUID userId,
        String accountName,
        AccountType accountType,
        AccountStatus status,
        Instant updatedAt,
        long sourceVersion
) {
}
