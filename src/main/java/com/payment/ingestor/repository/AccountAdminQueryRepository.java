package com.payment.ingestor.repository;

import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.AdminAccountSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountAdminQueryRepository {

    Page<AdminAccountResponse> findAccountsForAdminView(AdminAccountSearchCriteria criteria, Pageable pageable);

}
