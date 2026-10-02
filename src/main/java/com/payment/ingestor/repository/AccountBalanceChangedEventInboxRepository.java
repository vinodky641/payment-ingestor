package com.payment.ingestor.repository;

import com.payment.ingestor.entity.AccountBalanceChangedEventInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountBalanceChangedEventInboxRepository extends JpaRepository<AccountBalanceChangedEventInbox, UUID> {

    boolean existsById(UUID eventId);

}
