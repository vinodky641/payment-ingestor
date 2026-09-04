package com.payment.ingestor.service;

import com.payment.ingestor.dto.payment.AcceptedResponse;
import com.payment.ingestor.dto.payment.PaymentRequest;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.Payment;
import com.payment.ingestor.entity.PaymentOutbox;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.event.PaymentEvent;
import com.payment.ingestor.exception.*;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.repository.AccountRepository;
import com.payment.ingestor.repository.PaymentOutboxRepository;
import com.payment.ingestor.repository.PaymentRepository;
import com.payment.ingestor.repository.UserRepository;
import com.payment.ingestor.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
public class PaymentIngestionService {

    private final PaymentRepository paymentRepository;
    private final PaymentIdGenerator paymentIdGenerator;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper mapper;

    @Transactional
    public AcceptedResponse createPayment(
            String idempotencyKey,
            AppUserDetails userDetails,
            PaymentRequest request) {

        UUID userId = userDetails.getUserId();
        validateIdempotencyKey(idempotencyKey);
        String debitAccountId = request.debitAccountId().trim();
        String creditAccountId = request.creditAccountId().trim();
        String currency = request.currency().trim().toUpperCase(Locale.ROOT);

        // * Check whether this idempotency key was already used by this user
        Payment existingPayment = paymentRepository.findByIdempotencyKeyAndUserId(
                idempotencyKey,
                userId
        ).orElse(null);

        if (existingPayment != null) {
            // Same idempotency key and same request. Return the original response
            if (isSamePaymentRequest(existingPayment, request, currency)) {
                return new AcceptedResponse(
                        existingPayment.getPaymentId(),
                        PAYMENT_ACCEPTED_STATUS,
                        existingPayment.getReceivedAt()
                );
            }
            // Same idempotency key but different payment request
            throw new IdempotencyConflictException(
                    IDEMPOTENCY_KEY_FIELD,
                    IDEMPOTENCY_KEY_IS_ALREADY_USED_WITH_DIFFERENT_PAYMENT_REQUEST + idempotencyKey
            );
        }

        // Validate debit and credit accounts are different
        if (debitAccountId.equals(creditAccountId)) {

            throw new InvalidPaymentException(
                    CREDIT_ACCOUNT_ID_FIELD,
                    DEBIT_AND_CREDIT_ACCOUNTS_MUST_BE_DIFFERENT + creditAccountId
            );
        }

        // Load authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedUserException(
                                EMAIL_FIELD,
                                UNAUTHORIZED_USER_NOT_PERMITTED
                        )
                );

        //Accept debit account only if debitAccountId belongs to JWT userId
        Account debitAccount = accountRepository.findByAccountIdAndUserId(debitAccountId, userId)
                .orElseThrow(() -> new AccountNotFoundException(
                                DEBIT_ACCOUNT_ID_FIELD,
                                DEBIT_ACCOUNT_NOT_FOUND + debitAccountId
                        )
                );

        // Validate debit account status
        if (debitAccount.getStatus() == AccountStatus.SUSPENDED) {
            throw new AccountSuspendedException(
                    DEBIT_ACCOUNT_ID_FIELD,
                    DEBIT_ACCOUNT_IS_SUSPENDED + debitAccountId
            );
        }

        // Find credit account
        Account creditAccount = accountRepository.findByAccountId(creditAccountId)
                .orElseThrow(() -> new AccountNotFoundException(
                                CREDIT_ACCOUNT_ID_FIELD,
                                CREDIT_ACCOUNT_NOT_FOUND + creditAccountId
                        )
                );

        // Validate credit account status.
        if (creditAccount.getStatus() == AccountStatus.SUSPENDED) {
            throw new AccountSuspendedException(
                    CREDIT_ACCOUNT_ID_FIELD,
                    CREDIT_ACCOUNT_IS_SUSPENDED + creditAccountId
            );
        }

        // Generate unique payment ID
        String paymentId = paymentIdGenerator.generate();

        // Create Payment entity
        Instant receivedAt = Instant.now();
        Payment payment = new Payment(
                paymentId,
                idempotencyKey,
                debitAccount.getAccountId(),
                creditAccount.getAccountId(),
                request.amount(),
                currency,
                receivedAt,
                user
        );

        // Payment and PaymentOutbox must be saved in the same database transaction
        // The @Transactional annotation on this method guarantees that both operations succeed or both fail

        Payment savedPayment;
        try {
            // Save Payment
            savedPayment = paymentRepository.save(payment);
        } catch (Exception e) {
            throw new IllegalStateException(COULD_NOT_CREATE_PAYMENT_REQUEST, e);
        }

        UUID eventId = UUID.randomUUID();
        // Create PaymentEvent
        PaymentEvent paymentEvent = new PaymentEvent(
                eventId,
                savedPayment.getPaymentId(),
                savedPayment.getDebitAccountId(),
                savedPayment.getCreditAccountId(),
                savedPayment.getAmount(),
                savedPayment.getCurrency().toUpperCase(),
                Instant.now(),
                savedPayment.getUser().getId()
        );

        try {
            // Create PaymentOutbox entry here
            // Save PaymentOutbox
            paymentOutboxRepository.save(
                    new PaymentOutbox(
                            eventId,
                            paymentId,
                            PAYMENT_EVENT_TYPE,
                            mapper.writeValueAsString(paymentEvent)
                    )
            );
        } catch (Exception e) {
            throw new IllegalStateException(COULD_NOT_CREATE_PAYMENT_EVENT, e);
        }

        // Return response
        return new AcceptedResponse(
                savedPayment.getPaymentId(),
                PAYMENT_ACCEPTED_STATUS,
                savedPayment.getReceivedAt()
        );
    }


    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidPaymentException(
                    IDEMPOTENCY_KEY_FIELD,
                    IDEMPOTENCY_KEY_HEADER_REQUIRED
            );
        }
        if (idempotencyKey.length() > 64) {
            throw new InvalidPaymentException(
                    IDEMPOTENCY_KEY_FIELD,
                    IDEMPOTENCY_KEY_TOO_LONG
            );
        }
    }

    private boolean isSamePaymentRequest(Payment payment, PaymentRequest request, String normalizedCurrency) {

        return payment.getDebitAccountId().equals(request.debitAccountId().trim())
                && payment.getCreditAccountId().equals(request.creditAccountId().trim())
                && payment.getAmount().compareTo(request.amount()) == 0
                && payment.getCurrency().equals(normalizedCurrency);
    }

}
