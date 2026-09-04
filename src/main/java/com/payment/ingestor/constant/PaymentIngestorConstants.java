package com.payment.ingestor.constant;

import java.math.BigDecimal;
import java.time.Duration;

public final class PaymentIngestorConstants {

    // Prevent instantiation
    private PaymentIngestorConstants() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    // App user's registration and login related constants
    public static final String EMAIL_FIELD = "email";
    public static final String EMAIL_ALREADY_EXISTS_ERROR = "Email already exists";
    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered: ";
    public static final String INVALID_CREDENTIALS_ERROR = "Invalid credentials";
    public static final String INVALID_CREDENTIALS_FIELD = "email or password";
    public static final String INVALID_CREDENTIALS_EMAIL_OR_PASSWORD = "Invalid credentials email or password";
    public static final String USER_ACCOUNT_LOCKED_ERROR = "User account locked";
    public static final String USER_ACCOUNT_FIELD = "user account";
    public static final String USER_ACCOUNT_IS_LOCKED = "User account is temporarily locked: ";
    public static final String USER_ACCOUNT_DISABLED_ERROR = "User account disabled";
    public static final String USER_ACCOUNT_IS_DISABLED = "Account is disabled. Contact support.";
    public static final int USER_MAX_FAILED_ATTEMPTS = 5;
    public static final Duration USER_LOCK_DURATION = Duration.ofMinutes(15);
    public static final String TOKEN_TYPE_BEARER = "Bearer";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String JWT_TOKEN_STARTS_WITH_BEARER = "Bearer ";
    public static final int JWT_TOKEN_VALUE_BEGIN_INDEX_SEVEN = 7;
    public static final String UNAUTHORIZED_USER_ERROR = "Unauthorized user";
    public static final String UNAUTHORIZED_USER_NOT_PERMITTED = "Unauthorized user not permitted";
    public static final String SAME_ACCOUNT_TYPE = "Same account type";
    public static final String DUPLICATE_ACCOUNT_ERROR = "Duplicate account";
    public static final String USER_ALREADY_HAS_SAME_ACCOUNT_TYPE = "User already has an account of type: ";

    // Kafka constants
    public static final String KAFKA_TOPIC_NAME = "payments.submitted";
    public static final int KAFKA_TOPIC_PARTITIONS = 12;
    public static final int KAFKA_TOPIC_REPLICAS = 1;
    public static final String KAFKA_PAYMENT_OUTBOX_PUBLISHER_TIME_INTERVAL = "${outbox.poll-ms:250}";

    // Database constants
    public static final String USERS_TABLE_NAME = "users";
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String PAYMENT_TABLE_NAME = "payments";
    public static final String PAYMENT_OUTBOX_TABLE_NAME = "payment_outbox";

    // Payment outbox status constants
    public static final String PUBLISH_PAYMENT_TO_KAFKA_STATUS_PENDING = "PENDING";
    public static final String PUBLISH_PAYMENT_TO_KAFKA_STATUS_PUBLISHED = "PUBLISHED";

    // Payment validation constants
    public static final String VALIDATION_FAILED_ERROR = "Validation failed";
    public static final String ACCOUNT_SUSPENDED_ERROR = "Account suspended";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ACCOUNT_ID_FIELD = "accountId";
    public static final String ACCOUNT_NOT_FOUND = "Account not found: ";
    public static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found";
    public static final String DEBIT_ACCOUNT_ID_FIELD = "debitAccountId";
    public static final String DEBIT_ACCOUNT_NOT_FOUND = "Debit account not found: ";
    public static final String CREDIT_ACCOUNT_ID_FIELD = "creditAccountId";
    public static final String CREDIT_ACCOUNT_NOT_FOUND = "Credit account not found: ";
    public static final String DEBIT_ACCOUNT_IS_SUSPENDED = "Debit account is suspended: ";
    public static final String CREDIT_ACCOUNT_IS_SUSPENDED = "Credit account is suspended: ";
    public static final String PAYMENT_EVENT_TYPE = "PaymentEvent";
    public static final String COULD_NOT_CREATE_PAYMENT_REQUEST = "Could not create payment request";
    public static final String COULD_NOT_CREATE_PAYMENT_EVENT = "Could not create payment event";
    public static final String PAYMENT_ACCEPTED_STATUS = "ACCEPTED";
    public static final BigDecimal USER_FINANCIAL_ACCOUNT_INITIAL_BALANCE = BigDecimal.valueOf(10000).setScale(4);
    public static final String USER_FINANCIAL_ACCOUNT_ID_PREFIX = "ACC-";
    public static final String USER_FINANCIAL_PAYMENT_ID_PREFIX = "PAY-";
    public static final String IDEMPOTENCY_KEY_CONFLICT_ERROR = "Idempotency key conflict";
    public static final String IDEMPOTENCY_KEY_FIELD = "Idempotency-Key";
    public static final String IDEMPOTENCY_KEY_IS_ALREADY_USED_WITH_DIFFERENT_PAYMENT_REQUEST = "Idempotency key is already used for a different payment request: ";
    public static final String DEBIT_AND_CREDIT_ACCOUNTS_MUST_BE_DIFFERENT = "Debit and credit accounts must be different: ";
    public static final String IDEMPOTENCY_KEY_HEADER_REQUIRED = "Idempotency-Key header is required";
    public static final String IDEMPOTENCY_KEY_TOO_LONG = "Idempotency-Key must not exceed 64 characters";

    // Security constants
    public static final String API_ENDPOINT_AUTH_SIGNUP = "/api/auth/signup";
    public static final String API_ENDPOINT_AUTH_LOGIN = "/api/auth/login";
    public static final String API_ENDPOINT_ACTUATOR_HEALTH = "/actuator/health";
    public static final String API_ENDPOINT_ACTUATOR_METRICS = "/actuator/metrics";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String JWT_SECRET = "${security.jwt.secret}";
    public static final String JWT_EXPIRATION_TIME = "${security.jwt.expiration-ms}";
    public static final String JWT_USER_ID_CLAIM = "userId";
    public static final String JWT_EMAIL_CLAIM = "email";
    public static final String JWT_ROLES_CLAIM = "roles";

}
