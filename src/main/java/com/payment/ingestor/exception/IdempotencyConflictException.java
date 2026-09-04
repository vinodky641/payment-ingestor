package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class IdempotencyConflictException extends RuntimeException {

    private final String field;

    public IdempotencyConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

}
