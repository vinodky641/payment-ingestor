package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class AccountBalanceReplicaConflictException extends RuntimeException {

    private final String field;

    public AccountBalanceReplicaConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

}