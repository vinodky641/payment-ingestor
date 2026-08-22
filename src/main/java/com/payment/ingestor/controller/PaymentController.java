package com.payment.ingestor.controller;

import com.payment.ingestor.dto.AcceptedResponse;
import com.payment.ingestor.dto.PaymentRequest;
import com.payment.ingestor.service.PaymentIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentIngestionService paymentIngestionService;

    public PaymentController(PaymentIngestionService paymentIngestionService) {
        this.paymentIngestionService = paymentIngestionService;
    }

    @PostMapping
    public ResponseEntity<AcceptedResponse> submit(@Valid @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentIngestionService.submit(paymentRequest));
    }
}
