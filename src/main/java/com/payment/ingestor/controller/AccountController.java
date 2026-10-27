package com.payment.ingestor.controller;

import com.payment.ingestor.dto.account.AccountResponse;
import com.payment.ingestor.dto.account.CreateAccountRequest;
import com.payment.ingestor.dto.account.RecipientAccountPageResponse;
import com.payment.ingestor.enums.AccountType;
import com.payment.ingestor.exception.ApiErrorResponse;
import com.payment.ingestor.security.AppUserDetails;
import com.payment.ingestor.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(
        name = "Accounts",
        description = "User's financial account management APIs"
)
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new financial account",
            description = """
                    Creates a new financial account for the authenticated user.
                    The account owner is determined from the authenticated JWT and
                    cannot be supplied by the client.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Account created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed",
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
                    responseCode = "409",
                    description = "Account could not be created because of a conflicting account request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
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

    @Operation(
            summary = "Get all financial accounts of the authenticated user",
            description = """
                    Retrieves all financial accounts belonging to the currently authenticated user.
                    The user is identified from the authenticated JWT; the client does not
                    provide a user ID.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AccountResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required or JWT is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AccountResponse>> getAllAccounts(

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        List<AccountResponse> accounts = accountService.getAllAccounts(userDetails);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Get all financial accounts by status",
            description = """
                    Retrieves all financial accounts belonging to the authenticated user
                    that match the specified account status.
                    The user is identified from the authenticated JWT.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AccountResponse.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid account status",
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AccountResponse>> getAllAccountsByStatus(

            @RequestParam(name = "status", required = true)
            String status,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        List<AccountResponse> accounts = accountService.getAllAccountsByStatus(status, userDetails);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Get financial account by ID",
            description = """
                    Retrieves a financial account by its account ID for the authenticated user.
                    The account must belong to the currently authenticated user.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class)
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
                    responseCode = "404",
                    description = "Account not found or does not belong to the authenticated user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{accountId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AccountResponse> getAccount(

            @PathVariable
            String accountId,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        AccountResponse response = accountService.getAccount(accountId, userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get recipient accounts",
            description = """
                    Retrieves a paginated list of active accounts belonging to users
                    other than the authenticated user. Supports optional search,
                    account type, and currency filters.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Recipient accounts retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RecipientAccountPageResponse.class)
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/recipients")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipientAccountPageResponse> getRecipientAccounts(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            AccountType accountType,

            @RequestParam(required = false)
            String currency,

            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "userName",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable,

            @AuthenticationPrincipal
            AppUserDetails userDetails
    ) {

        RecipientAccountPageResponse response = accountService.getRecipientAccounts(
                search,
                accountType,
                currency,
                pageable,
                userDetails.getUserId()
        );

        return ResponseEntity.ok(response);
    }

}
