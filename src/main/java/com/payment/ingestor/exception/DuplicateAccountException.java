package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class DuplicateAccountException extends RuntimeException {

    private final String field;

    public DuplicateAccountException(String field, String message) {
        super(message);
        this.field = field;
    }

}

