package com.payment.ingestor.exception;

import org.springframework.http.HttpStatus;

public class AccountValidationException extends RuntimeException {

    private final HttpStatus status;

    public AccountValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}
