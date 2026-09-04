package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponse(
        String accountId,
        String accountName,
        AccountType accountType,
        BigDecimal accountBalance,
        AccountStatus status,
        String currency,
        LocalDate openedDate
) {
}
