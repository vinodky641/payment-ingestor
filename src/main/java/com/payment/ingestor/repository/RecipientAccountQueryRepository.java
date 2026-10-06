package com.payment.ingestor.repository;

import com.payment.ingestor.dto.account.RecipientAccountResponse;
import com.payment.ingestor.dto.account.RecipientAccountSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipientAccountQueryRepository {

    Page<RecipientAccountResponse> findRecipientAccounts(RecipientAccountSearchCriteria criteria, Pageable pageable);

}
