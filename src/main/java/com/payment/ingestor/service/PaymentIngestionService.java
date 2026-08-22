package com.payment.ingestor.service;

import com.payment.ingestor.dto.AcceptedResponse;
import com.payment.ingestor.dto.PaymentRequest;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.PaymentOutbox;
import com.payment.ingestor.event.PaymentEvent;
import com.payment.ingestor.exception.AccountValidationException;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.repository.AccountRepository;
import com.payment.ingestor.repository.PaymentOutboxRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
public class PaymentIngestionService {

    private final AccountRepository accountRepository;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper mapper;

    public PaymentIngestionService(
            AccountRepository accountRepository,
            PaymentOutboxRepository paymentOutboxRepository,
            ObjectMapper mapper ) {

        this.accountRepository = accountRepository;
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.mapper = mapper;
    }

    @Transactional
    public AcceptedResponse submit(PaymentRequest paymentRequest) {
        Account debit = accountRepository.findById(
                paymentRequest.debitAccountId()
        ).orElseThrow(
                () -> new AccountValidationException(
                        HttpStatus.NOT_FOUND,
                        DEBIT_ACCOUNT_NOT_FOUND + paymentRequest.debitAccountId()
                )
        );

        Account credit = accountRepository.findById(
                paymentRequest.creditAccountId()
        ).orElseThrow(
                () -> new AccountValidationException(
                        HttpStatus.NOT_FOUND,
                        CREDIT_ACCOUNT_NOT_FOUND + paymentRequest.creditAccountId()
                )
        );

        if (debit.getStatus() == AccountStatus.SUSPENDED) {
            throw new AccountValidationException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    ACCOUNT_IS_SUSPENDED + debit.getAccountId()
            );
        }

        if (credit.getStatus() == AccountStatus.SUSPENDED) {
            throw new AccountValidationException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    ACCOUNT_IS_SUSPENDED + credit.getAccountId()
            );
        }

        if (debit.getAccountId().equals(credit.getAccountId())) {
            throw new AccountValidationException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    DEBIT_AND_CREDIT_ACCOUNTS_MUST_BE_DIFFERENT
            );
        }

        String paymentId = PAYMENT_ID_PREFIX + UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        PaymentEvent event = new PaymentEvent(
                eventId,
                paymentId,
                debit.getAccountId(),
                credit.getAccountId(),
                paymentRequest.amount(),
                paymentRequest.currency().toUpperCase(),
                Instant.now()
        );

        try {
            paymentOutboxRepository.save(
                    new PaymentOutbox(
                            eventId,
                            paymentId,
                            PAYMENT_EVENT_TYPE,
                            mapper.writeValueAsString(event)
                    )
            );
        } catch (Exception e) {
            throw new IllegalStateException(COULD_NOT_CREATE_PAYMENT_EVENT, e);
        }

        return new AcceptedResponse(paymentId, PAYMENT_ACCEPTED_STATUS);
    }
}

