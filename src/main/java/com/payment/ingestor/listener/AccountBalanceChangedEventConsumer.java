package com.payment.ingestor.listener;

import com.payment.ingestor.event.AccountBalanceChangedEvent;
import com.payment.ingestor.service.AccountBalanceChangedEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountBalanceChangedEventConsumer {

    private final AccountBalanceChangedEventHandler handler;

    @KafkaListener(
            topics = KAFKA_TOPIC_NAME_FOR_ACCOUNTS_BALANCE_CHANGED,
            groupId = KAFKA_CONSUMER_BALANCE_GROUP_ID,
            containerFactory = ACCOUNTS_BALANCE_CHANGED_KAFKA_LISTENER_CONTAINER_FACTORY
    )
    public void consume(AccountBalanceChangedEvent event) {
        log.debug("Received AccountBalanceChangedEvent. eventId={}, accountId={}, paymentId={}, balanceVersion={}",
                event.eventId(),
                event.accountId(),
                event.paymentId(),
                event.balanceVersion()
        );
        handler.handle(event);
    }

    @DltHandler
    public void handleDeadLetter(AccountBalanceChangedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.error("AccountBalanceChangedEvent moved to DLT after retries were exhausted. " +
                        "eventId={}, accountId={}, paymentId={}, userId()={}, balanceVersion={}, topic={}",
                event.eventId(),
                event.accountId(),
                event.paymentId(),
                event.userId(),
                event.balanceVersion(),
                topic
        );
    }

}
