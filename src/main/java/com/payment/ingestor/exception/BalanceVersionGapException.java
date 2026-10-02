package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class BalanceVersionGapException extends RuntimeException {

    private final String field;

    public BalanceVersionGapException(String field, String message) {
        super(message);
        this.field = field;
    }

}