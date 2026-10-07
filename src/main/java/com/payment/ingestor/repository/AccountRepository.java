package com.payment.ingestor.repository;

import com.payment.ingestor.entity.Account;
import com.payment.ingestor.enums.AccountStatus;
import com.payment.ingestor.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID>, AccountAdminQueryRepository, RecipientAccountQueryRepository {

    Optional<Account> findByAccountId(String accountId);

    List<Account> findAllByUserId(UUID userId);

    List<Account> findAllByUserIdAndStatus(UUID userId, AccountStatus status);

    boolean existsByUserIdAndAccountType(UUID userId, AccountType accountType);

    Optional<Account> findByAccountIdAndUserId(String accountId, UUID userId);

}
