package com.payment.ingestor.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant timestamp,
        int status,
        String error,
        String path,
        List<Violation> violations
) {
}
