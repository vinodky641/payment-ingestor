package com.payment.ingestor.exception;

import lombok.Getter;

@Getter
public class ReplicaNotReadyException extends RuntimeException {

    private final String field;

    public ReplicaNotReadyException(String field, String message) {
        super(message);
        this.field = field;
    }

}