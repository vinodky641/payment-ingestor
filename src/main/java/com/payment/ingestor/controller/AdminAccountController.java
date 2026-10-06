package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.PageResponse;
import com.payment.ingestor.dto.account.UpdateAccountRequest;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import com.payment.ingestor.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            AccountStatus status,

            @RequestParam(required = false)
            AccountType accountType,

            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "openedDate",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        PageResponse<AdminAccountResponse> response = adminAccountService.getAccountsForAdminView(
                search,
                status,
                accountType,
                pageable
        );

        return ResponseEntity.ok(response);
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
