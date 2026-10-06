package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountType;

import java.util.UUID;

public record RecipientAccountSearchCriteria(
        String search,
        AccountType accountType,
        String currency,
        UUID currentUserId
) {
}
