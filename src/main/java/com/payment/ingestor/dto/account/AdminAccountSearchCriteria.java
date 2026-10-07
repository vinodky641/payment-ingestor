package com.payment.ingestor.dto.account;

import com.payment.ingestor.enums.AccountStatus;
import com.payment.ingestor.enums.AccountType;

public record AdminAccountSearchCriteria(
        String search,
        AccountStatus status,
        AccountType accountType
) {
}

