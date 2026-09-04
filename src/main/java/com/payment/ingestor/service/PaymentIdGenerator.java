package com.payment.ingestor.service;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Component;

import static com.payment.ingestor.constant.PaymentIngestorConstants.USER_FINANCIAL_PAYMENT_ID_PREFIX;

@Component
public class PaymentIdGenerator {

    public String generate() {
        return USER_FINANCIAL_PAYMENT_ID_PREFIX + UlidCreator.getMonotonicUlid().toString();
    }

}
