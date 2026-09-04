package com.payment.ingestor.dto.payment;

import java.time.Instant;

public record AcceptedResponse(
        String paymentId,
        String status,
        Instant receivedAt
) {
}
