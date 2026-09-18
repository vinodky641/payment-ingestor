package com.payment.ingestor.repository;

import com.payment.ingestor.dto.account.AdminAccountResponse;
import com.payment.ingestor.dto.account.RecipientAccountResponse;
import com.payment.ingestor.entity.Account;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountId(String accountId);

    List<Account> findAllByUserId(UUID userId);

    List<Account> findAllByUserIdAndStatus(UUID userId, AccountStatus status);

    boolean existsByUserIdAndAccountType(UUID userId, AccountType accountType);

    Optional<Account> findByAccountIdAndUserId(String accountId, UUID userId);

    @Query("""
            SELECT new com.payment.ingestor.dto.account.RecipientAccountResponse(
                u.id,
                u.displayName,
                a.accountId,
                a.accountName,
                a.accountType,
                a.currency
            )
            FROM Account a
            JOIN a.user u
            WHERE u.id <> :currentUserId
              AND a.status = :status
            ORDER BY a.accountId ASC
            """)
    Page<RecipientAccountResponse> findRecipientAccounts(
            @Param("currentUserId") UUID currentUserId,
            @Param("status") AccountStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT new com.payment.ingestor.dto.account.AdminAccountResponse(
            a.accountId,
            u.id,
            u.displayName,
            a.accountName,
            a.accountType,
            a.status,
            a.accountBalance,
            a.currency,
            a.openedDate
        )
        FROM Account a
        JOIN a.user u
        ORDER BY a.accountId ASC
        """)
    Page<AdminAccountResponse> findAllForAdmin(Pageable pageable);

}
