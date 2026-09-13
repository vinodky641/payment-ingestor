package com.payment.ingestor.service;

import com.payment.ingestor.entity.User;
import com.payment.ingestor.entity.UserUpdatedOutbox;
import com.payment.ingestor.event.UserUpdatedEvent;
import com.payment.ingestor.model.OutboxStatus;
import com.payment.ingestor.repository.UserUpdatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.COULD_NOT_CREATE_USER_UPDATED_EVENT;
import static com.payment.ingestor.constant.PaymentIngestorConstants.EVENT_TYPE_USER_UPDATED;

@Service
@RequiredArgsConstructor
public class UserUpdatedOutboxService {

    private final UserUpdatedOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void createOutboxEvent(User user) {

        try {
            UUID eventId = UUID.randomUUID();

            UserUpdatedEvent event = new UserUpdatedEvent(
                    eventId,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    user.getDisplayName(),
                    user.getStatus(),
                    user.isEmailVerified(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getSourceVersion()
            );

            String payload = objectMapper.writeValueAsString(event);

            UserUpdatedOutbox outbox = UserUpdatedOutbox.builder()
                    .eventId(eventId)
                    .userId(user.getId())
                    .eventType(EVENT_TYPE_USER_UPDATED)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();

            repository.save(outbox);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    COULD_NOT_CREATE_USER_UPDATED_EVENT,
                    ex
            );
        }
    }

}
