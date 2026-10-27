package com.payment.ingestor.service;

import com.payment.ingestor.dto.account.*;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.enums.AccountStatus;
import com.payment.ingestor.enums.AccountType;
import com.payment.ingestor.exception.AccountNotFoundException;
import com.payment.ingestor.exception.AppIllegalArgumentException;
import com.payment.ingestor.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public AdminAccountPageResponse getAccountsForAdminView(
            String search,
            AccountStatus status,
            AccountType accountType,
            Pageable pageable
    ) {
        if (pageable.getPageNumber() < 0) {
            throw new AppIllegalArgumentException(
                    PAGE_FIELD,
                    PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO
            );
        }

        if (pageable.getPageSize() < 1 || pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new AppIllegalArgumentException(
                    PAGE_SIZE_FIELD,
                    PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND + MAX_PAGE_SIZE
            );
        }

        AdminAccountSearchCriteria criteria = new AdminAccountSearchCriteria(search, status, accountType);
        Page<AdminAccountResponse> result = accountRepository.findAccountsForAdminView(criteria, pageable);

        return new AdminAccountPageResponse(
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
