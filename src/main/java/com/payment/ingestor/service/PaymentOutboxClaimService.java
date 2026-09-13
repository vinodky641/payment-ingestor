package com.payment.ingestor.service;

import com.payment.ingestor.entity.PaymentOutbox;
import com.payment.ingestor.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOutboxClaimService {

    private final PaymentOutboxRepository repository;

    @Transactional
    public List<PaymentOutbox> claimBatch(int batchSize) {

        if (batchSize <= 0) {
            log.info("Batch size must be greater than 0. Provided batchSize={}", batchSize);
            return List.of();
        }

        List<PaymentOutbox> events = repository.findPendingForUpdate(batchSize);
        if (events.isEmpty()) {
            log.debug("No pending payment outbox events found to claim");
            return events;
        }

        events.forEach(PaymentOutbox::markProcessing);
        repository.saveAll(events);
        log.debug("Claimed {} payment outbox events", events.size());
        return events;
    }

}