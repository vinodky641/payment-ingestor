package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AdminAccountResponse(
        String accountId,
        UUID userId,
        String userName,
        String accountName,
        AccountType accountType,
        AccountStatus status,
        BigDecimal accountBalance,
        String currency,
        LocalDate openedDate
) {
}
