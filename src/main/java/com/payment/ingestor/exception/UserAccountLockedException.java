package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class UserAccountLockedException extends RuntimeException {

    private final String field;

    public UserAccountLockedException(String field, String message) {
        super(message);
        this.field = field;
    }
    
}
