package com.payment.ingestor.controller;

import com.payment.ingestor.dto.payment.AcceptedResponse;
import com.payment.ingestor.dto.payment.PaymentRequest;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.PaymentIngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentIngestionService paymentIngestionService;

    @PostMapping
    public ResponseEntity<AcceptedResponse> createPayment(

            @RequestHeader(name = "Idempotency-Key", required = true)
            String idempotencyKey,

            @AuthenticationPrincipal
            AppUserDetails userDetails,

            @Valid @RequestBody
            PaymentRequest paymentRequest) {

        AcceptedResponse response = paymentIngestionService.createPayment(
                idempotencyKey,
                userDetails,
                paymentRequest
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

}
