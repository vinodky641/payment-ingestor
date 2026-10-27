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

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountBalanceChangedEventConsumer {

    private final AccountBalanceChangedEventHandler handler;

    @KafkaListener(
            topics = "${spring.kafka.topics.accounts.balance-changed}",
            groupId = "${app.kafka.consumer.balance-group-id}",
            containerFactory = "accountBalanceChangedKafkaListenerContainerFactory"
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
