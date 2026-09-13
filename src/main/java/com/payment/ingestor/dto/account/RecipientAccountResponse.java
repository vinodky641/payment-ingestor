package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountType;

import java.util.UUID;

public record RecipientAccountResponse(
        UUID userId,
        String userName,
        String accountId,
        String accountName,
        AccountType accountType,
        String currency
) {
}
