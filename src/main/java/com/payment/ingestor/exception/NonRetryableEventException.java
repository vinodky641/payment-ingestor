package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class NonRetryableEventException extends RuntimeException {

    private final String field;

    public NonRetryableEventException(String field, String message) {
        super(message);
        this.field = field;
    }

}
