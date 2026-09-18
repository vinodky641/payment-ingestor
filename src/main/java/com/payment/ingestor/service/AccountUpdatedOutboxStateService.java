package com.payment.ingestor.service;

import com.payment.ingestor.repository.AccountUpdatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUpdatedOutboxStateService {

    private final AccountUpdatedOutboxRepository repository;

    @Transactional
    public void markPublished(UUID eventId) {

        int updatedRows = repository.markPublishedIfProcessing(eventId, Instant.now());
        if (updatedRows == 0) {
            log.warn(
                    "AccountUpdatedOutbox was not marked PUBLISHED because it is no longer PROCESSING. eventId={}",
                    eventId
            );
        }
    }

    @Transactional
    public void resetToPending(UUID eventId) {

        int updatedRows = repository.resetToPendingIfProcessing(eventId);
        if (updatedRows == 0) {
            log.warn(
                    "AccountUpdatedOutbox was not reset to PENDING because it is no longer PROCESSING. eventId={}",
                    eventId
            );
        }
    }

}
