package com.payment.ingestor.constant;

public final class PaymentIngestorConstants {

    // Prevent instantiation
    private PaymentIngestorConstants() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    // Kafka constants
    public static final String KAFKA_TOPIC_NAME = "payments.submitted";
    public static final int KAFKA_TOPIC_PARTITIONS = 12;
    public static final int KAFKA_TOPIC_REPLICAS = 1;
    // Database constants
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String PAYMENT_OUTBOX_TABLE_NAME = "payment_outbox";
    // Payment outbox status constants
    public static final String PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING = "PENDING";
    public static final String PUBLISH_PAYMENT_TO_KAFKA_STATUS_PUBLISHED = "PUBLISHED";

    // Payment validation constants
    public static final String DEBIT_ACCOUNT_NOT_FOUND = "Debit account not found: ";
    public static final String CREDIT_ACCOUNT_NOT_FOUND = "Credit account not found: ";
    public static final String ACCOUNT_IS_SUSPENDED = "Account is suspended: ";
    public static final String DEBIT_AND_CREDIT_ACCOUNTS_MUST_BE_DIFFERENT = "Debit and credit accounts must be different";
    public static final String PAYMENT_ID_PREFIX = "PAY-";
    public static final String PAYMENT_EVENT_TYPE = "PaymentEvent";
    public static final String COULD_NOT_CREATE_PAYMENT_EVENT = "Could not create payment event";
    public static final String PAYMENT_ACCEPTED_STATUS = "ACCEPTED";
}
