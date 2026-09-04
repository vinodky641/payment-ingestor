package com.payment.ingestor.exception;

public record Violation(
        String field,
        String message
) {
}