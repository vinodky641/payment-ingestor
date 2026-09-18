package com.payment.ingestor.service;

import com.payment.ingestor.entity.User;
import com.payment.ingestor.entity.UserCreatedOutbox;
import com.payment.ingestor.event.UserCreatedEvent;
import com.payment.ingestor.model.OutboxStatus;
import com.payment.ingestor.repository.UserCreatedOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.COULD_NOT_CREATE_USER_CREATED_EVENT;
import static com.payment.ingestor.constant.PaymentIngestorConstants.EVENT_TYPE_USER_CREATED;

@Service
@RequiredArgsConstructor
public class UserCreatedOutboxService {

    private final UserCreatedOutboxRepository repository;
    private final ObjectMapper objectMapper;

    public void createOutboxEvent(User user) {

        try {
            UUID eventId = UUID.randomUUID();

            UserCreatedEvent event = new UserCreatedEvent(
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
                    user.getRole(),
                    user.getSourceVersion()
            );

            String payload = objectMapper.writeValueAsString(event);

            UserCreatedOutbox outbox = UserCreatedOutbox.builder()
                    .eventId(eventId)
                    .userId(user.getId())
                    .eventType(EVENT_TYPE_USER_CREATED)
                    .payload(payload)
                    .status(OutboxStatus.PENDING)
                    .build();

            repository.save(outbox);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    COULD_NOT_CREATE_USER_CREATED_EVENT,
                    ex
            );
        }
    }

}
