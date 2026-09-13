package com.payment.ingestor.service;

import com.payment.ingestor.entity.UserUpdatedOutbox;
import com.payment.ingestor.repository.UserUpdatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUpdatedOutboxClaimService {

    private final UserUpdatedOutboxRepository repository;

    @Transactional
    public List<UserUpdatedOutbox> claimBatch(int batchSize) {

        if (batchSize <= 0) {
            log.info("Batch size must be greater than 0. Provided batchSize={}", batchSize);
            return List.of();
        }

        List<UserUpdatedOutbox> events = repository.findPendingForUpdate(batchSize);
        if (events.isEmpty()) {
            log.debug("No pending user updated outbox events found to claim");
            return events;
        }

        events.forEach(UserUpdatedOutbox::markProcessing);
        repository.saveAll(events);
        log.debug("Claimed {} user updated outbox events", events.size());
        return events;
    }

}
