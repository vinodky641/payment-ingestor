package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;

public record AdminAccountSearchCriteria(
        String search,
        AccountStatus status,
        AccountType accountType
) {
}

