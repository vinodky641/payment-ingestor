package com.payment.ingestor.service;

import com.payment.ingestor.repository.AccountCreatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCreatedOutboxStateService {

    private final AccountCreatedOutboxRepository repository;

    @Transactional
    public void markPublished(UUID eventId) {

        int updatedRows = repository.markPublishedIfProcessing(eventId, Instant.now());
        if (updatedRows == 0) {
            log.warn(
                    "AccountCreatedOutbox was not marked PUBLISHED because it is no longer PROCESSING. eventId={}",
                    eventId
            );
        }
    }

    @Transactional
    public void resetToPending(UUID eventId) {

        int updatedRows = repository.resetToPendingIfProcessing(eventId);
        if (updatedRows == 0) {
            log.warn(
                    "AccountCreatedOutbox was not reset to PENDING because it is no longer PROCESSING. eventId={}",
                    eventId
            );
        }
    }

}