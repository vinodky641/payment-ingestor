package com.payment.ingestor.repository;

import com.payment.ingestor.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByIdempotencyKeyAndUserId(String idempotencyKey, UUID userId);
    
}
