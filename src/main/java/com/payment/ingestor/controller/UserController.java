package com.payment.ingestor.controller;

import com.payment.ingestor.dto.user.UpdateUserRequest;
import com.payment.ingestor.dto.user.UserResponse;
import com.payment.ingestor.exception.ApiErrorResponse;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "Update user profile API"
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Update user profile",
            description = "Updates the authenticated user's profile information."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Authenticated user not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMyProfile(

            @Valid
            @RequestBody
            UpdateUserRequest request,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        UserResponse response = userService.updateProfile(request, userDetails);
        return ResponseEntity.ok(response);
    }

}
