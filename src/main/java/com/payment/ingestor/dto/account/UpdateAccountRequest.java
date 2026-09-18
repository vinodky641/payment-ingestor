package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(

        @NotBlank(message = "Account name is required")
        @Size(
                max = 150,
                message = "Account name must not exceed 150 characters"
        )
        String accountName,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotNull(message = "Account status is required")
        AccountStatus status
) {
}