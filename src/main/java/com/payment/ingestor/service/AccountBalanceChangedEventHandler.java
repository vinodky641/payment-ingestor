package com.payment.ingestor.service;

import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.AccountBalanceChangedEventInbox;
import com.payment.ingestor.event.AccountBalanceChangedEvent;
import com.payment.ingestor.exception.AccountBalanceReplicaConflictException;
import com.payment.ingestor.exception.BalanceVersionGapException;
import com.payment.ingestor.exception.NonRetryableEventException;
import com.payment.ingestor.exception.ReplicaNotReadyException;
import com.payment.ingestor.repository.AccountBalanceChangedEventInboxRepository;
import com.payment.ingestor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceChangedEventHandler {

    private final AccountRepository accountRepository;
    private final AccountBalanceChangedEventInboxRepository inboxRepository;

    @Transactional
    public void handle(AccountBalanceChangedEvent event) {

        validate(event);
        // Idempotency check
        if (inboxRepository.existsById(event.eventId())) {
            log.debug(
                    "Ignoring duplicate AccountBalanceChangedEvent. eventId={}, accountId={}, paymentId={}, balanceVersion={}",
                    event.eventId(),
                    event.accountId(),
                    event.paymentId(),
                    event.balanceVersion()
            );
            return;
        }

        // Account must already exist in the Ingestor replica
        Account account = accountRepository.findByAccountId(event.accountId())
                .orElseThrow(() -> new ReplicaNotReadyException(
                                ACCOUNT_ID_FIELD,
                                ACCOUNT_REPLICA_NOT_READY + event.accountId()
                        )
                );

        // Identity consistency check
        if (!account.getUser().getId().equals(event.userId())) {
            throw new AccountBalanceReplicaConflictException(
                    USER_ID_FIELD,
                    "Account userId mismatch. accountId = " + event.accountId() + ", accountUserId = " + account.getUser().getId()
                            + ", eventUserId = " + event.userId()
            );
        }

        // Currency is immutable in the Account
        if (!account.getCurrency().equalsIgnoreCase(event.currency())) {
            throw new AccountBalanceReplicaConflictException(
                    CURRENCY_FIELD,
                    "Account currency mismatch. accountId = " + event.accountId() + ", accountCurrency = " + account.getCurrency() +
                            ", eventCurrency = " + event.currency()
            );
        }

        long currentVersion = account.getBalanceVersion();
        long incomingVersion = event.balanceVersion();

        // Duplicate OR stale event
        if (incomingVersion <= currentVersion) {
            log.debug("Ignoring stale AccountBalanceChangedEvent. eventId={}, accountId={}, currentVersion={}, incomingVersion={}",
                    event.eventId(),
                    event.accountId(),
                    currentVersion,
                    incomingVersion
            );
            saveInbox(event);
            return;
        }

        // We only accept the immediately next version
        if (incomingVersion > currentVersion + 1) {
            throw new BalanceVersionGapException(
                    BALANCE_VERSION_FIELD,
                    "Balance version gap detected. accountId = " + event.accountId() + ", currentVersion = " + currentVersion +
                            ", incomingVersion = " + incomingVersion
            );
        }


        // At this point incomingVersion == currentVersion + 1
        // Therefore previousBalance must match the current replica
        if (account.getAccountBalance().compareTo(event.previousBalance()) != 0) {
            throw new AccountBalanceReplicaConflictException(
                    PREVIOUS_BALANCE_FIELD,
                    "Previous balance does not match account replica. accountId = " + event.accountId() +
                            ", currentBalance = " + account.getAccountBalance() +
                            ", eventPreviousBalance = " + event.previousBalance() +
                            ", balanceVersion = " + incomingVersion
            );
        }


        // newBalance is authoritative.
        account.applyBalanceChange(event.newBalance(), incomingVersion);
        accountRepository.save(account);
        saveInbox(event);
        log.info("Applied AccountBalanceChangedEvent. eventId={}, accountId={}, paymentId={}, previousBalance={}, newBalance={}, balanceVersion={}",
                event.eventId(),
                event.accountId(),
                event.paymentId(),
                event.previousBalance(),
                event.newBalance(),
                event.balanceVersion()
        );
    }

    private void saveInbox(AccountBalanceChangedEvent event) {

        AccountBalanceChangedEventInbox inbox = AccountBalanceChangedEventInbox.builder()
                .eventId(event.eventId())
                .accountId(event.accountId())
                .userId(event.userId())
                .previousBalance(event.previousBalance())
                .newBalance(event.newBalance())
                .currency(event.currency())
                .paymentId(event.paymentId())
                .changedAt(event.changedAt())
                .balanceVersion(event.balanceVersion())
                .processedAt(Instant.now())
                .build();

        inboxRepository.save(inbox);
    }

    private void validate(AccountBalanceChangedEvent event) {

        if (event == null) {
            throw new NonRetryableEventException(
                    ACCOUNT_BALANCE_CHANGED_EVENT_FIELD,
                    ACCOUNT_BALANCE_CHANGED_EVENT_MUST_NOT_BE_NULL
            );
        }

        if (event.eventId() == null) {
            throw new NonRetryableEventException(
                    EVENT_ID_FIELD,
                    EVENT_ID_MUST_NOT_BE_NULL
            );
        }

        if (event.accountId() == null || event.accountId().isBlank()) {
            throw new NonRetryableEventException(
                    ACCOUNT_ID_FIELD,
                    ACCOUNT_ID_MUST_NOT_BE_BLANK
            );
        }

        if (event.userId() == null) {
            throw new NonRetryableEventException(
                    USER_ID_FIELD,
                    USER_ID_MUST_NOT_BE_NULL
            );
        }

        if (event.previousBalance() == null) {
            throw new NonRetryableEventException(
                    PREVIOUS_BALANCE_FIELD,
                    PREVIOUS_BALANCE_MUST_NOT_BE_NULL
            );
        }

        if (event.newBalance() == null) {
            throw new NonRetryableEventException(
                    NEW_BALANCE_FIELD,
                    NEW_BALANCE_MUST_NOT_BE_NULL
            );
        }

        if (event.previousBalance().signum() < 0) {
            throw new NonRetryableEventException(
                    PREVIOUS_BALANCE_FIELD,
                    PREVIOUS_BALANCE_CAN_NOT_BE_NEGATIVE
            );
        }

        if (event.newBalance().signum() < 0) {
            throw new NonRetryableEventException(
                    NEW_BALANCE_FIELD,
                    NEW_BALANCE_CAN_NOT_BE_NEGATIVE
            );
        }

        if (event.currency() == null || event.currency().isBlank() || event.currency().length() != 3) {
            throw new NonRetryableEventException(
                    CURRENCY_FIELD,
                    CURRENCY_MUST_BE_A_3CHARACTER_ISO_CURRENCY_CODE
            );
        }

        if (event.paymentId() == null || event.paymentId().isBlank()) {
            throw new NonRetryableEventException(
                    PAYMENT_ID_FIELD,
                    PAYMENT_ID_MUST_NOT_BE_BLANK
            );
        }

        if (event.changedAt() == null) {
            throw new NonRetryableEventException(
                    CHANGED_AT_FIELD,
                    CHANGED_AT_MUST_NOT_BE_NULL
            );
        }

        if (event.balanceVersion() <= 0) {
            throw new NonRetryableEventException(
                    BALANCE_VERSION_FIELD,
                    BALANCE_VERSION_MUST_BE_GREATER_THAN_ZERO
            );
        }
    }

}