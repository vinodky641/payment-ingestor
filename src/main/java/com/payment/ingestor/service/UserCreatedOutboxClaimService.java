package com.payment.ingestor.service;

import com.payment.ingestor.entity.UserCreatedOutbox;
import com.payment.ingestor.repository.UserCreatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreatedOutboxClaimService {

    private final UserCreatedOutboxRepository repository;

    @Transactional
    public List<UserCreatedOutbox> claimBatch(int batchSize) {

        if (batchSize <= 0) {
            log.info("Batch size must be greater than 0. Provided batchSize={}", batchSize);
            return List.of();
        }

        List<UserCreatedOutbox> events = repository.findPendingForUpdate(batchSize);
        if (events.isEmpty()) {
            log.debug("No pending user created outbox events found to claim");
            return events;
        }

        events.forEach(UserCreatedOutbox::markProcessing);
        repository.saveAll(events);
        log.debug("Claimed {} user created outbox events", events.size());
        return events;
    }
    
}
