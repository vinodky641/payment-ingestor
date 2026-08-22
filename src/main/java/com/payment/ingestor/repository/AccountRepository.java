package com.payment.ingestor.repository;

import com.payment.ingestor.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
}
