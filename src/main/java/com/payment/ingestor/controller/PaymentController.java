package com.payment.ingestor.controller;

import com.payment.ingestor.dto.payment.AcceptedResponse;
import com.payment.ingestor.dto.payment.PaymentRequest;
import com.payment.ingestor.exception.ApiErrorResponse;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.PaymentOutboxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payments",
        description = "Submit payments API"
)
public class PaymentController {

    private final PaymentOutboxService paymentOutboxService;

    @Operation(
            summary = "Submit a payment",
            description = """
                    Submits a payment for asynchronous processing.
                    
                    The request is validated against the debit and
                    credit accounts. If valid, the payment is published
                    to Kafka and HTTP 202 Accepted is returned.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Payment accepted for processing",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AcceptedResponse.class)
                    )
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
                    responseCode = "404",
                    description = "Debit or credit account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Duplicate payment",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Account is suspended",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AcceptedResponse> createPayment(

            @RequestHeader(name = "Idempotency-Key", required = true)
            String idempotencyKey,

            @AuthenticationPrincipal
            AppUserDetails userDetails,

            @Valid @RequestBody
            PaymentRequest paymentRequest
    ) {

        AcceptedResponse response = paymentOutboxService.createPayment(
                idempotencyKey,
                userDetails,
                paymentRequest
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
