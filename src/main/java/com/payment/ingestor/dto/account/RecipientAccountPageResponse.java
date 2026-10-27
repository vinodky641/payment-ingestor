package com.payment.ingestor.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated list of recipient's active accounts")
public record RecipientAccountPageResponse(

        @Schema(description = "Active accounts on the current page")
        List<RecipientAccountResponse> content,

        @Schema(description = "Zero-based page index", example = "0")
        int page,

        @Schema(description = "Page size", example = "20")
        int size,

        @Schema(description = "Total recipient's active accounts", example = "145")
        long totalElements,

        @Schema(description = "Total number of pages", example = "8")
        int totalPages,

        @Schema(description = "Whether this is the first page")
        boolean first,

        @Schema(description = "Whether this is the last page")
        boolean last
) {
}
