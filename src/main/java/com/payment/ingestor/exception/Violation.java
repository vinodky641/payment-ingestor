package com.payment.ingestor.exception;

import io.swagger.v3.oas.annotations.media.Schema;

public record Violation(

        @Schema(description = "Name of the field that failed validation", example = "email")
        String field,

        @Schema(description = "Validation error message", example = "Must be a valid email address")
        String message
) {
}