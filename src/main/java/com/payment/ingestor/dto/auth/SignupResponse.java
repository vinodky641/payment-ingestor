package com.payment.ingestor.dto.auth;

import java.util.UUID;

public record SignupResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName
) {
}
