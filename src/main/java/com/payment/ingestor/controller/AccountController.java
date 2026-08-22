package com.payment.ingestor.controller;

import com.payment.ingestor.dto.AccountResponse;
import com.payment.ingestor.repository.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> get(@PathVariable String accountId) {
        return accountRepository.findById(accountId)
                .map(a -> ResponseEntity.ok(
                        new AccountResponse(
                                a.getAccountId(),
                                a.getAccountName(),
                                a.getAccountType(),
                                a.getAccountBalance(),
                                a.getStatus(),
                                a.getCurrency(),
                                a.getOpenedDate()
                        )
                    )
                ).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
