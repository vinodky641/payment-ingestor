package com.payment.ingestor.service;

import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.AccountUpdatedOutbox;
import com.payment.ingestor.event.AccountUpdatedEvent;
import com.payment.ingestor.model.OutboxStatus;
import com.payment.ingestor.repository.AccountUpdatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.COULD_NOT_CREATE_ACCOUNT_UPDATED_EVENT;
import static com.payment.ingestor.constant.PaymentIngestorConstants.EVENT_TYPE_ACCOUNT_UPDATED;

@Service
@RequiredArgsConstructor
public class AccountUpdatedOutboxService {

    private final AccountUpdatedOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void createOutboxEvent(Account account) {

        try {
            UUID eventId = UUID.randomUUID();

            AccountUpdatedEvent event = new AccountUpdatedEvent(
                    eventId,
                    account.getAccountId(),
                    account.getUser().getId(),
                    account.getAccountName(),
                    account.getAccountType(),
                    account.getStatus(),
                    account.getCurrency(),
                    account.getOpenedDate(),
                    account.getUpdatedAt(),
                    account.getSourceVersion()
            );

            String payload = objectMapper.writeValueAsString(event);

            AccountUpdatedOutbox outbox = AccountUpdatedOutbox.builder()
                    .eventId(eventId)
                    .accountId(account.getAccountId())
                    .eventType(EVENT_TYPE_ACCOUNT_UPDATED)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();

            repository.save(outbox);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    COULD_NOT_CREATE_ACCOUNT_UPDATED_EVENT,
                    ex
            );
        }
    }

}
