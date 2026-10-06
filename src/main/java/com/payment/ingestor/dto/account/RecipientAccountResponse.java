package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountType;

import java.util.UUID;

public record RecipientAccountResponse(
        String accountId,
        UUID userId,
        String userName,
        String email,
        String accountName,
        AccountType accountType,
        String currency
) {
}
