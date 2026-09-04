package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.CreateAccountRequest;
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
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(

            @AuthenticationPrincipal
            AppUserDetails userDetails,

            @Valid
            @RequestBody
            CreateAccountRequest request) {

        AccountResponse response = accountService.createAccount(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(

            @AuthenticationPrincipal
            AppUserDetails userDetails) {

        List<AccountResponse> accounts = accountService.getMyAccounts(userDetails);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/my/active")
    public ResponseEntity<List<AccountResponse>> getMyActiveAccounts(

            @AuthenticationPrincipal
            AppUserDetails userDetails) {

        List<AccountResponse> accounts = accountService.getMyActiveAccounts(userDetails);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(

            @PathVariable
            String accountId,

            @AuthenticationPrincipal
            AppUserDetails userDetails) {

        AccountResponse response = accountService.getAccount(accountId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
