package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class UnauthorizedUserException extends RuntimeException {

    private final String field;

    public UnauthorizedUserException(String field, String message) {
        super(message);
        this.field = field;
    }

}