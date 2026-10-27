package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.AdminAccountPageResponse;
import com.payment.ingestor.dto.account.UpdateAccountRequest;
import com.payment.ingestor.enums.AccountStatus;
import com.payment.ingestor.enums.AccountType;
import com.payment.ingestor.exception.ApiErrorResponse;
import com.payment.ingestor.service.AdminAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Admin Accounts",
        description = "User's financial account management by Admin APIs"
)
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @Operation(
            summary = "Get accounts for admin view",
            description = """
                    Retrieves a paginated list of accounts for administrators.
                    Supports filtering by search text, account status, and account type,
                    along with configurable sorting.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminAccountPageResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid pagination or search parameters",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. ADMIN role is required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminAccountPageResponse> getAccountsForAdminView(

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

        AdminAccountPageResponse response = adminAccountService.getAccountsForAdminView(
                search,
                status,
                accountType,
                pageable
        );

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update user's finance account by administrator",
            description = """
                    Updates the editable fields of an account.
                    Only users with the ADMIN role can perform this operation.
                    Account balance is managed by the payment processor and cannot
                    be modified through this endpoint.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed or invalid account update data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. ADMIN role is required",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account could not be updated because it was modified concurrently",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{accountId}")
    @PreAuthorize("hasRole('ADMIN')")
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
