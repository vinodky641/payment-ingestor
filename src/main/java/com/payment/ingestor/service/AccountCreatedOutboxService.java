package com.payment.ingestor.service;

import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.AccountCreatedOutbox;
import com.payment.ingestor.event.AccountCreatedEvent;
import com.payment.ingestor.model.OutboxStatus;
import com.payment.ingestor.repository.AccountCreatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.COULD_NOT_CREATE_ACCOUNT_CREATED_EVENT;
import static com.payment.ingestor.constant.PaymentIngestorConstants.EVENT_TYPE_ACCOUNT_CREATED;

@Service
@RequiredArgsConstructor
public class AccountCreatedOutboxService {

    private final AccountCreatedOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void createOutboxEvent(Account account) {

        try {
            UUID eventId = UUID.randomUUID();

            AccountCreatedEvent event = new AccountCreatedEvent(
                    eventId,
                    account.getAccountId(),
                    account.getUser().getId(),
                    account.getAccountName(),
                    account.getAccountType(),
                    account.getAccountBalance(),
                    account.getStatus(),
                    account.getCurrency(),
                    account.getOpenedDate(),
                    account.getSourceVersion()
            );

            String payload = objectMapper.writeValueAsString(event);

            AccountCreatedOutbox outbox = AccountCreatedOutbox.builder()
                    .eventId(eventId)
                    .accountId(account.getAccountId())
                    .eventType(EVENT_TYPE_ACCOUNT_CREATED)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();

            repository.save(outbox);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    COULD_NOT_CREATE_ACCOUNT_CREATED_EVENT,
                    ex
            );
        }
    }

}
