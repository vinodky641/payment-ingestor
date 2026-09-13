package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.CreateAccountRequest;
import com.payment.ingestor.dto.account.PageResponse;
import com.payment.ingestor.dto.account.RecipientAccountResponse;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(

            @Valid
            @RequestBody
            CreateAccountRequest request,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        AccountResponse response = accountService.createAccount(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        List<AccountResponse> accounts = accountService.getAllAccounts(userDetails);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccountsByStatus(

            @RequestParam(name = "status", required = true)
            String status,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        List<AccountResponse> accounts = accountService.getAllAccountsByStatus(status, userDetails);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(

            @PathVariable
            String accountId,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        AccountResponse response = accountService.getAccount(accountId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/recipients")
    public ResponseEntity<PageResponse<RecipientAccountResponse>> getRecipientAccounts(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {
        PageResponse<RecipientAccountResponse> response = accountService.getRecipientAccounts(
                page,
                size,
                userDetails
        );
        return ResponseEntity.ok(response);
    }

}
