package com.payment.ingestor.service;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.CreateAccountRequest;
import com.payment.ingestor.dto.account.PageResponse;
import com.payment.ingestor.dto.account.RecipientAccountResponse;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.AccountNotFoundException;
import com.payment.ingestor.exception.AppIllegalArgumentException;
import com.payment.ingestor.exception.DuplicateAccountException;
import com.payment.ingestor.exception.UnauthorizedUserException;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.repository.AccountRepository;
import com.payment.ingestor.repository.UserRepository;
import com.payment.ingestor.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountIdGenerator accountIdGenerator;
    private final UserRepository userRepository;
    private final AccountCreatedOutboxService accountCreatedOutboxService;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request, AppUserDetails userDetails) {

        // Get the user by using authenticated user ID from JWT
        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new UnauthorizedUserException(
                                EMAIL_FIELD,
                                UNAUTHORIZED_USER_NOT_PERMITTED
                        )
                );

        // Rule for one account per type
        if (accountRepository.existsByUserIdAndAccountType(userDetails.getUserId(), request.accountType())) {
            throw new DuplicateAccountException(
                    SAME_ACCOUNT_TYPE,
                    USER_ALREADY_HAS_SAME_ACCOUNT_TYPE + request.accountType()
            );
        }

        //Generate a unique account ID
        String accountId = accountIdGenerator.generate();
        // Create the account.
        Account account = new Account(
                accountId,
                request.accountName().trim(),
                request.accountType(),
                USER_FINANCIAL_ACCOUNT_INITIAL_BALANCE,
                AccountStatus.ACTIVE,
                request.currency().trim().toUpperCase(),
                user
        );

        // Persist the account
        Account savedAccount = accountRepository.save(account);
        accountCreatedOutboxService.createOutboxEvent(savedAccount);
        return AccountResponse.from(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts(AppUserDetails userDetails) {

        // Get the authenticated user's ID from JWT
        UUID userId = userDetails.getUserId();

        // Find all accounts belonging to this user
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccountsByStatus(String status, AppUserDetails userDetails) {

        if (status == null || status.isBlank()) {
            throw new AppIllegalArgumentException(
                    STATUS_FIELD,
                    STATUS_CAN_NOT_BE_NULL_OR_BLANK
            );
        }

        // Get the authenticated user's ID from JWT
        UUID userId = userDetails.getUserId();
        List<Account> accounts = new ArrayList<>();
        List<AccountResponse> accountsResponse = new ArrayList<>();

        // Return only ACTIVE accounts belonging to the logged-in user
        if (status.equalsIgnoreCase(AccountStatus.ACTIVE.toString())) {
            accounts = accountRepository.findAllByUserIdAndStatus(userId, AccountStatus.ACTIVE);
        }

        // Return only SUSPENDED accounts belonging to the logged-in user
        if (status.equalsIgnoreCase(AccountStatus.SUSPENDED.toString())) {
            accounts = accountRepository.findAllByUserIdAndStatus(userId, AccountStatus.SUSPENDED);
        }

        if (!accounts.isEmpty()) {
            accountsResponse = accounts.stream()
                    .map(AccountResponse::from)
                    .toList();
        }
        return accountsResponse;
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountId, AppUserDetails userDetails) {

        // Get the account by using accountId and authenticated user ID from JWT
        Account account = accountRepository.findByAccountIdAndUserId(
                accountId,
                userDetails.getUserId()
        ).orElseThrow(() -> new AccountNotFoundException(
                ACCOUNT_ID_FIELD,
                ACCOUNT_NOT_FOUND + accountId
        ));

        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public PageResponse<RecipientAccountResponse> getRecipientAccounts(
            int page,
            int size,
            AppUserDetails userDetails
    ) {
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

        // Get the authenticated user's ID from JWT
        UUID currentUserId = userDetails.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<RecipientAccountResponse> result = accountRepository.findRecipientAccounts(
                currentUserId,
                AccountStatus.ACTIVE,
                pageable
        );

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

}
