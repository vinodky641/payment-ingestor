package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class AccountSuspendedException extends RuntimeException {

    private final String field;

    public AccountSuspendedException(String field, String message) {
        super(message);
        this.field = field;
    }
    
}
