package com.payment.ingestor.service;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.CreateAccountRequest;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.AccountNotFoundException;
import com.payment.ingestor.exception.DuplicateAccountException;
import com.payment.ingestor.exception.UnauthorizedUserException;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.repository.AccountRepository;
import com.payment.ingestor.repository.UserRepository;
import com.payment.ingestor.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountIdGenerator accountIdGenerator;
    private final UserRepository userRepository;

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
                LocalDate.now(),
                user
        );

        // Persist the account
        Account savedAccount = accountRepository.save(account);
        return mapToResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts(AppUserDetails userDetails) {

        // Get the authenticated user's ID from JWT
        UUID userId = userDetails.getUserId();

        // Find all accounts belonging to this user
        return accountRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getMyActiveAccounts(AppUserDetails userDetails) {

        // Get the authenticated user's ID from JWT
        UUID userId = userDetails.getUserId();

        // Return only ACTIVE accounts belonging to the logged-in user
        return accountRepository.findAllByUserIdAndStatus(userId, AccountStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
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

        return mapToResponse(account);
    }

    private AccountResponse mapToResponse(Account account) {

        return new AccountResponse(
                account.getAccountId(),
                account.getAccountName(),
                account.getAccountType(),
                account.getAccountBalance(),
                account.getStatus(),
                account.getCurrency(),
                account.getOpenedDate()
        );
    }

}
