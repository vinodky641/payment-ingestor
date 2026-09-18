package com.payment.ingestor.service;

import com.payment.ingestor.entity.AccountUpdatedOutbox;
import com.payment.ingestor.repository.AccountUpdatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUpdatedOutboxClaimService {

    private final AccountUpdatedOutboxRepository repository;

    @Transactional
    public List<AccountUpdatedOutbox> claimBatch(int batchSize) {

        if (batchSize <= 0) {
            log.info("Batch size must be greater than 0. Provided batchSize={}", batchSize);
            return List.of();
        }

        List<AccountUpdatedOutbox> events = repository.findPendingForUpdate(batchSize);
        if (events.isEmpty()) {
            log.debug("No pending account updated outbox events found to claim");
            return events;
        }

        events.forEach(AccountUpdatedOutbox::markProcessing);
        repository.saveAll(events);
        log.debug("Claimed {} account updated outbox events", events.size());
        return events;
    }

}
