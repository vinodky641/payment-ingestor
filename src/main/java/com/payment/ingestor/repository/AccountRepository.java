package com.payment.ingestor.repository;

import com.payment.ingestor.entity.Account;
import com.payment.ingestor.model.AccountStatus;
import com.payment.ingestor.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountId(String accountId);

    List<Account> findAllByUserId(UUID userId);

    List<Account> findAllByUserIdAndStatus(UUID userId, AccountStatus status);

    boolean existsByUserIdAndAccountType(UUID userId, AccountType accountType);

    Optional<Account> findByAccountIdAndUserId(String accountId, UUID userId);

}
