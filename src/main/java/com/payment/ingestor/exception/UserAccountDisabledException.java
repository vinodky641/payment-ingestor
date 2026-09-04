package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class UserAccountDisabledException extends RuntimeException {

    private final String field;

    public UserAccountDisabledException(String field, String message) {
        super(message);
        this.field = field;
    }

}
