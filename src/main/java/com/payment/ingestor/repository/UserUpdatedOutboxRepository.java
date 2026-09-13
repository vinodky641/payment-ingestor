package com.payment.ingestor.repository;

import com.payment.ingestor.entity.UserUpdatedOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserUpdatedOutboxRepository extends JpaRepository<UserUpdatedOutbox, UUID> {

    @Query(
            value = """
                    SELECT *
                    FROM users_updated_outbox
                    WHERE status = 'PENDING'
                    ORDER BY created_at
                    LIMIT :batchSize
                    FOR UPDATE SKIP LOCKED
                    """,
            nativeQuery = true
    )
    List<UserUpdatedOutbox> findPendingForUpdate(@Param("batchSize") int batchSize);

    @Modifying
    @Query(
            value = """
                    UPDATE users_updated_outbox
                    SET status = 'PUBLISHED',
                        published_at = :publishedAt,
                        claimed_at = NULL
                    WHERE event_id = :eventId
                      AND status = 'PROCESSING'
                    """,
            nativeQuery = true
    )
    int markPublishedIfProcessing(@Param("eventId") UUID eventId, @Param("publishedAt") Instant publishedAt);

    @Modifying
    @Query(
            value = """
                    UPDATE users_updated_outbox
                    SET status = 'PENDING',
                        claimed_at = NULL
                    WHERE event_id = :eventId
                      AND status = 'PROCESSING'
                    """,
            nativeQuery = true
    )
    int resetToPendingIfProcessing(@Param("eventId") UUID eventId);

    @Modifying
    @Query(
            value = """
                    UPDATE users_updated_outbox
                    SET status = 'PENDING',
                        claimed_at = NULL
                    WHERE status = 'PROCESSING'
                      AND claimed_at < :staleBefore
                    """,
            nativeQuery = true
    )
    int resetStaleEvents(@Param("staleBefore") Instant staleBefore);

}
