package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class InvalidPaymentException extends RuntimeException {

    private final String field;

    public InvalidPaymentException(String field, String message) {
        super(message);
        this.field = field;
    }

}