package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.PageResponse;
import com.payment.ingestor.dto.account.UpdateAccountRequest;
import com.payment.ingestor.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/accounts")
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AdminAccountResponse>> getAccountsForAdminView(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return ResponseEntity.ok(adminAccountService.getAccountsForAdminView(page, size)
        );
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccountByAdmin(
            @PathVariable
            String accountId,

            @Valid
            @RequestBody
            UpdateAccountRequest request
    ) {
        return ResponseEntity.ok(adminAccountService.updateAccountByAdmin(accountId, request));
    }

}
