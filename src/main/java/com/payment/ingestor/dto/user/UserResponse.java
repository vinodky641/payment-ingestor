package com.payment.ingestor.dto.user;

import com.payment.ingestor.entity.User;
import com.payment.ingestor.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String displayName,
        UserStatus status,
        boolean emailVerified,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getDisplayName(),
                user.getStatus(),
                user.isEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
