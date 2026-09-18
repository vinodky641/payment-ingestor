package com.payment.ingestor.service;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.PageResponse;
import com.payment.ingestor.dto.account.UpdateAccountRequest;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.exception.AccountNotFoundException;
import com.payment.ingestor.exception.AppIllegalArgumentException;
import com.payment.ingestor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
public class AdminAccountService {

    private final AccountRepository accountRepository;
    private final AccountUpdatedOutboxService accountUpdatedOutboxService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public PageResponse<AdminAccountResponse> getAccountsForAdminView(int page, int size) {

        if (page < 0) {
            throw new AppIllegalArgumentException(
                    PAGE_FIELD,
                    PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new AppIllegalArgumentException(
                    PAGE_SIZE_FIELD,
                    PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND + MAX_PAGE_SIZE
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "accountId")
        );

        Page<AdminAccountResponse> result = accountRepository.findAllForAdmin(pageable);
        return new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public AccountResponse updateAccountByAdmin(
            String accountId,
            UpdateAccountRequest request
    ) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                                ACCOUNT_ID_FIELD,
                                ACCOUNT_NOT_FOUND + accountId
                        )
                );

        account.updateAccount(
                request.accountName(),
                request.accountType(),
                request.status()
        );

        Account savedAccount = accountRepository.save(account);
        accountUpdatedOutboxService.createOutboxEvent(savedAccount);
        return AccountResponse.from(savedAccount);
    }

}
