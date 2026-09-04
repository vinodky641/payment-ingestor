package com.payment.ingestor.dto.account;

import com.payment.ingestor.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(

        @NotBlank(message = "Account name is required")
        @Size(max = 255, message = "Account name must not exceed 255 characters")
        String accountName,

        @NotNull(message = "Account type is required")
        AccountType accountType,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency
) {
}
