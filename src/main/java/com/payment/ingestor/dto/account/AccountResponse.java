package com.payment.ingestor.dto.account;

import com.payment.ingestor.entity.Account;
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

    public static AccountResponse from(Account account) {

        return new AccountResponse(
                account.getAccountId(),
                account.getAccountName(),
                account.getAccountType(),
                account.getAccountBalance(),
                account.getStatus(),
                account.getCurrency(),
                account.getOpenedDate()
        );
    }

}
