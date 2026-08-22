package com.payment.ingestor.dto;

public record AcceptedResponse(
        String paymentId,
        String status
) {
}
