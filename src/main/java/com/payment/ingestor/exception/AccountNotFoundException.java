package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {

    private final String field;

    public AccountNotFoundException(String field, String message) {
        super(message);
        this.field = field;
    }

}
