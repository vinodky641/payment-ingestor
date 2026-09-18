package com.payment.ingestor.event;

import com.payment.ingestor.model.Role;
import com.payment.ingestor.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent(
        UUID eventId,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String displayName,
        UserStatus status,
        boolean emailVerified,
        Instant createdAt,
        Instant updatedAt,
        Role role,
        Long sourceVersion
) {
}
