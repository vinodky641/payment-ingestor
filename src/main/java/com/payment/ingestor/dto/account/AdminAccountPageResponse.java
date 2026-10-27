package com.payment.ingestor.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated list of accounts for Admin view")
public record AdminAccountPageResponse(

        @Schema(description = "accounts on the current page")
        List<AdminAccountResponse> content,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size", example = "20")
        int size,

        @Schema(description = "Total number of accounts", example = "145")
        long totalElements,

        @Schema(description = "Total number of pages", example = "8")
        int totalPages,

        @Schema(description = "Whether this is the first page")
        boolean first,

        @Schema(description = "Whether this is the last page")
        boolean last
) {
}
