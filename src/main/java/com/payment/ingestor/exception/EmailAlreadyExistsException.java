package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyExistsException extends RuntimeException {

    private final String field;

    public EmailAlreadyExistsException(String field, String message) {
        super(message);
        this.field = field;
    }

}

