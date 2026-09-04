package com.payment.ingestor.dto.auth;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email
) {
}
