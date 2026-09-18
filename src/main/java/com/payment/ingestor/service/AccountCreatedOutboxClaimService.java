package com.payment.ingestor.service;

import com.payment.ingestor.entity.AccountCreatedOutbox;
import com.payment.ingestor.repository.AccountCreatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCreatedOutboxClaimService {

    private final AccountCreatedOutboxRepository repository;

    @Transactional
    public List<AccountCreatedOutbox> claimBatch(int batchSize) {

        if (batchSize <= 0) {
            log.info("Batch size must be greater than 0. Provided batchSize={}", batchSize);
            return List.of();
        }

        List<AccountCreatedOutbox> events = repository.findPendingForUpdate(batchSize);
        if (events.isEmpty()) {
            log.debug("No pending account created outbox events found to claim");
            return events;
        }

        events.forEach(AccountCreatedOutbox::markProcessing);
        repository.saveAll(events);
        log.debug("Claimed {} account created outbox events", events.size());
        return events;
    }

}
