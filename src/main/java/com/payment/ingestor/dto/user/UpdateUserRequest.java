package com.payment.ingestor.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 100, message = "First name must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 100, message = "Last name must not exceed 100 characters")
        String lastName,

        @Size(max = 30, message = "Phone number must not exceed 30 characters")
        String phoneNumber,

        @NotBlank(message = "Display name is required")
        @Size(max = 150, message = "Display name must not exceed 150 characters")
        String displayName
) {
}