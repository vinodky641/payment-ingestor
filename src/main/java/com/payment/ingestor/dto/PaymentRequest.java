package com.payment.ingestor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotBlank
        String debitAccountId,

        @NotBlank
        String creditAccountId,

        @NotNull @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank @Pattern(regexp = "[A-Za-z]{3}")
        String currency
) {
}
