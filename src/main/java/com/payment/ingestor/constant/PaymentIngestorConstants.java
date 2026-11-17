package com.payment.ingestor.constant;

import java.math.BigDecimal;
import java.time.Duration;

public final class PaymentIngestorConstants {

    // Prevent instantiation
    private PaymentIngestorConstants() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    // Config constants
    public static final String RETENTION_MS = "retention.ms";
    public static final String JWT_PRIVATE_KEY = "${app.security.jwt.private-key}";
    public static final String JWT_PRIVATE_KEY_LOAD_ERROR = "Unable to load JWT RSA private key";
    public static final String JWT_PUBLIC_KEY = "${app.security.jwt.public-key}";
    public static final String JWT_PUBLIC_KEY_LOAD_ERROR = "Unable to load JWT RSA public key";
    public static final String JWT_SIGNATURE_ALGORITHM_RSA = "RSA";
    public static final String JWT_EXPIRATION_MS = "${app.security.jwt.expiration-ms}";
    public static final String JWT_ISSUER = "${app.security.jwt.issuer}";
    public static final String JWT_AUDIENCES = "${app.security.jwt.audiences}";
    public static final String JWT_AUDIENCE_FIELD = "audience";
    public static final String JWT_AUDIENCE_REQUIRED = "At least one JWT audience must be configured";
    public static final String JWT_AUD_CLAIM = "aud";
    public static final String JWT_ISSUER_FIELD = "issuer";
    public static final String JWT_INVALID_ISSUER = "Invalid JWT issuer";
    public static final String JWT_INVALID_AUDIENCE = "Invalid JWT audience";

    // App user's registration and login related constants
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
    public static final String STATUS_CAN_NOT_BE_NULL_OR_BLANK = "Status cannot be null or blank";
    public static final String PAGE_FIELD = "page";
    public static final String PAGE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO = "Page must be greater than or equal to 0";
    public static final String PAGE_SIZE_FIELD = "size";
    public static final int MAX_PAGE_SIZE = 100;
    public static final String PAGE_SIZE_MUST_BE_BETWEEN_ONE_AND = "Page size must be between 1 and ";

    // Account AND User fields constants
    public static final String ACCOUNT_ID_FIELD = "accountId";
    public static final String ACCOUNT_NAME_FIELD = "accountName";
    public static final String ACCOUNT_TYPE_FIELD = "accountType";
    public static final String USER_NAME_FIELD = "userName";
    public static final String FIRST_NAME_FIELD = "firstName";
    public static final String LAST_NAME_FIELD = "lastName";
    public static final String ID_FIELD = "id";
    public static final String EMAIL_FIELD = "email";
    public static final String USER_FIELD = "user";
    public static final String ACCOUNT_BALANCE_FIELD = "accountBalance";
    public static final String ACCOUNT_STATUS_FIELD = "status";
    public static final String ACCOUNT_CURRENCY_FIELD = "currency";
    public static final String ACCOUNT_OPENED_DATE_FIELD = "openedDate";
    public static final String UNSUPPORTED_SORT_FIELD = "Unsupported sort field: ";

    // Kafka constants
    public static final String KAFKA_BOOTSTRAP_SERVERS = "${spring.kafka.bootstrap-servers}";
    public static final String KAFKA_TOPIC_FOR_USERS_CREATED = "spring.kafka.topics.users-created";
    public static final String KAFKA_TOPIC_FOR_USERS_UPDATED = "spring.kafka.topics.users-updated";
    public static final String KAFKA_TOPIC_FOR_ACCOUNTS_CREATED = "spring.kafka.topics.accounts-created";
    public static final String KAFKA_TOPIC_FOR_ACCOUNTS_UPDATED = "spring.kafka.topics.accounts-updated";
    public static final String KAFKA_TOPIC_FOR_PAYMENTS_SUBMITTED = "spring.kafka.topics.payments-submitted";
    public static final String KAFKA_TOPIC_NAME_FOR_ACCOUNTS_BALANCE_CHANGED = "accounts.balance-changed";
    public static final String KAFKA_RETRY_TOPIC_FOR_ACCOUNTS_BALANCE_CHANGED = "app.kafka.retry-topics.accounts-balance-changed";
    public static final String TRUSTED_PACKAGE_OF_PAYMENT_EVENT = "com.payment.ingestor.event";

    public static final String OUTBOX_FOR_USERS_CREATED = "app.outbox.users-created";
    public static final String OUTBOX_FOR_USERS_UPDATED = "app.outbox.users-updated";
    public static final String OUTBOX_FOR_ACCOUNTS_CREATED = "app.outbox.accounts-created";
    public static final String OUTBOX_FOR_ACCOUNTS_UPDATED = "app.outbox.accounts-updated";
    public static final String OUTBOX_FOR_PAYMENTS = "app.outbox.payments";

    public static final String KAFKA_CONSUMER = "app.kafka.consumer";
    public static final String KAFKA_PRODUCER = "app.kafka.producer";
    public static final String KAFKA_RETRY_TOPICS_PRODUCER = "app.kafka.retry-topics.producer";

    public static final String KAFKA_CONSUMER_BALANCE_GROUP_ID = "${app.kafka.consumer.balance-group-id}";
    public static final String ACCOUNTS_BALANCE_CHANGED_KAFKA_LISTENER_CONTAINER_FACTORY = "accountBalanceChangedKafkaListenerContainerFactory";

    // Database constants
    public static final String USERS_TABLE_NAME = "users";
    public static final String USERS_CREATED_OUTBOX_TABLE_NAME = "users_created_outbox";
    public static final String USERS_UPDATED_OUTBOX_TABLE_NAME = "users_updated_outbox";
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String ACCOUNTS_CREATED_OUTBOX_TABLE_NAME = "accounts_created_outbox";
    public static final String ACCOUNTS_UPDATED_OUTBOX_TABLE_NAME = "accounts_updated_outbox";
    public static final String ACCOUNTS_BALANCE_CHANGED_EVENT_INBOX_TABLE_NAME = "accounts_balance_changed_event_inbox";
    public static final String PAYMENT_TABLE_NAME = "payments";
    public static final String PAYMENT_OUTBOX_TABLE_NAME = "payments_outbox";

    // Scheduled time interval related constants
    public static final String TIME_INTERVAL_FOR_RUNNING_PENDING_USERS_CREATED_PUBLISH = "${app.outbox.users-created.fixed-delay-ms:500}";
    public static final String TIME_INTERVAL_FOR_RUNNING_STALE_USERS_CREATED_RECOVERY = "${app.outbox.users-created.recovery.fixed-delay-ms:60000}";
    public static final String TIME_INTERVAL_FOR_RUNNING_PENDING_USERS_UPDATED_PUBLISH = "${app.outbox.users-updated.fixed-delay-ms:500}";
    public static final String TIME_INTERVAL_FOR_RUNNING_STALE_USERS_UPDATED_RECOVERY = "${app.outbox.users-updated.recovery.fixed-delay-ms:60000}";
    public static final String TIME_INTERVAL_FOR_RUNNING_PENDING_ACCOUNTS_CREATED_PUBLISH = "${app.outbox.accounts-created.fixed-delay-ms:500}";
    public static final String TIME_INTERVAL_FOR_RUNNING_STALE_ACCOUNTS_CREATED_RECOVERY = "${app.outbox.accounts-created.recovery.fixed-delay-ms:60000}";
    public static final String TIME_INTERVAL_FOR_RUNNING_PENDING_ACCOUNTS_UPDATED_PUBLISH = "${app.outbox.accounts-updated.fixed-delay-ms:500}";
    public static final String TIME_INTERVAL_FOR_RUNNING_STALE_ACCOUNTS_UPDATED_RECOVERY = "${app.outbox.accounts-updated.recovery.fixed-delay-ms:60000}";
    public static final String TIME_INTERVAL_FOR_RUNNING_PENDING_PAYMENTS_PUBLISH = "${app.outbox.payments.fixed-delay-ms:200}";
    public static final String TIME_INTERVAL_FOR_RUNNING_STALE_PAYMENTS_RECOVERY = "${app.outbox.payments.recovery.fixed-delay-ms:60000}";

    // Event types
    public static final String EVENT_TYPE_USER_CREATED = "UserCreated";
    public static final String EVENT_TYPE_USER_UPDATED = "UserUpdated";
    public static final String EVENT_TYPE_ACCOUNT_CREATED = "AccountCreated";
    public static final String EVENT_TYPE_ACCOUNT_UPDATED = "AccountUpdated";
    public static final String EVENT_TYPE_PAYMENT = "Payment";
    public static final String METRICS_TYPE = "type";

    // UserCreated Outbox related metrics constants
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_CLAIMED = "userCreated.outbox.events.claimed";
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_INFLIGHT = "userCreated.outbox.events.inflight";
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_PUBLISHED = "userCreated.outbox.events.published";
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_FAILED = "userCreated.outbox.events.failed";
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_RECOVERED = "userCreated.outbox.events.recovered";
    public static final String USER_CREATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY = "userCreated.outbox.kafka.publish.latency";

    // UserUpdated Outbox related metrics constants
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_CLAIMED = "userUpdated.outbox.events.claimed";
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_INFLIGHT = "userUpdated.outbox.events.inflight";
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_PUBLISHED = "userUpdated.outbox.events.published";
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_FAILED = "userUpdated.outbox.events.failed";
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_RECOVERED = "userUpdated.outbox.events.recovered";
    public static final String USER_UPDATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY = "userUpdated.outbox.kafka.publish.latency";

    // AccountCreated Outbox related metrics constants
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_CLAIMED = "accountCreated.outbox.events.claimed";
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_INFLIGHT = "accountCreated.outbox.events.inflight";
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_PUBLISHED = "accountCreated.outbox.events.published";
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_FAILED = "accountCreated.outbox.events.failed";
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_RECOVERED = "accountCreated.outbox.events.recovered";
    public static final String ACCOUNT_CREATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY = "accountCreated.outbox.kafka.publish.latency";

    // AccountUpdated Outbox related metrics constants
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_CLAIMED = "accountUpdated.outbox.events.claimed";
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_INFLIGHT = "accountUpdated.outbox.events.inflight";
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_PUBLISHED = "accountUpdated.outbox.events.published";
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_FAILED = "accountUpdated.outbox.events.failed";
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_RECOVERED = "accountUpdated.outbox.events.recovered";
    public static final String ACCOUNT_UPDATED_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY = "accountUpdated.outbox.kafka.publish.latency";

    // Payment Outbox related metrics constants
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_CLAIMED = "payment.outbox.events.claimed";
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_INFLIGHT = "payment.outbox.events.inflight";
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_PUBLISHED = "payment.outbox.events.published";
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_FAILED = "payment.outbox.events.failed";
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_RECOVERED = "payment.outbox.events.recovered";
    public static final String PAYMENT_OUTBOX_EVENTS_METRIC_KAFKA_PUBLISH_LATENCY = "payment.outbox.kafka.publish.latency";

    // Account Balance Changed Event Inbox validation constants
    public static final String USER_ID_FIELD = "userId";
    public static final String USER_ID_MUST_NOT_BE_NULL = "userId must not be null";
    public static final String CURRENCY_MUST_BE_A_3CHARACTER_ISO_CURRENCY_CODE = "currency must be a 3-character ISO currency code";
    public static final String BALANCE_VERSION_FIELD = "balanceVersion";
    public static final String PREVIOUS_BALANCE_FIELD = "previousBalance";
    public static final String PREVIOUS_BALANCE_MUST_NOT_BE_NULL = "previousBalance must not be null";
    public static final String PREVIOUS_BALANCE_CAN_NOT_BE_NEGATIVE = "previousBalance cannot be negative";
    public static final String NEW_BALANCE_FIELD = "newBalance";
    public static final String NEW_BALANCE_MUST_NOT_BE_NULL = "newBalance must not be null";
    public static final String NEW_BALANCE_CAN_NOT_BE_NEGATIVE = "newBalance cannot be negative";
    public static final String ACCOUNT_BALANCE_CHANGED_EVENT_FIELD = "accountBalanceChangedEvent";
    public static final String ACCOUNT_BALANCE_CHANGED_EVENT_MUST_NOT_BE_NULL = "AccountBalanceChangedEvent must not be null";
    public static final String EVENT_ID_FIELD = "eventId";
    public static final String EVENT_ID_MUST_NOT_BE_NULL = "eventId must not be null";
    public static final String ACCOUNT_ID_MUST_NOT_BE_BLANK = "accountId must not be blank";
    public static final String ACCOUNT_REPLICA_NOT_READY = "Account replica not ready: ";
    public static final String PAYMENT_ID_FIELD = "paymentId";
    public static final String PAYMENT_ID_MUST_NOT_BE_BLANK = "paymentId must not be blank";
    public static final String CHANGED_AT_FIELD = "changedAt";
    public static final String CHANGED_AT_MUST_NOT_BE_NULL = "changedAt must not be null";
    public static final String BALANCE_VERSION_MUST_BE_GREATER_THAN_ZERO = "balanceVersion must be greater than zero";

    // Payment validation constants
    public static final String VALIDATION_FAILED_ERROR = "Validation failed";
    public static final String ACCOUNT_SUSPENDED_ERROR = "Account suspended";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String ACCOUNT_NOT_FOUND = "Account not found: ";
    public static final String ACCOUNT_NOT_FOUND_ERROR = "Account not found";
    public static final String DEBIT_ACCOUNT_ID_FIELD = "debitAccountId";
    public static final String DEBIT_ACCOUNT_NOT_FOUND = "Debit account not found: ";
    public static final String CREDIT_ACCOUNT_ID_FIELD = "creditAccountId";
    public static final String CREDIT_ACCOUNT_NOT_FOUND = "Credit account not found: ";
    public static final String DEBIT_ACCOUNT_IS_SUSPENDED = "Debit account is suspended: ";
    public static final String CREDIT_ACCOUNT_IS_SUSPENDED = "Credit account is suspended: ";
    public static final String COULD_NOT_CREATE_PAYMENT_REQUEST = "Could not create payment request";
    public static final String COULD_NOT_CREATE_PAYMENT_EVENT = "Could not create payment event";
    public static final String COULD_NOT_CREATE_USER_CREATED_EVENT = "Could not create user created event";
    public static final String COULD_NOT_CREATE_USER_UPDATED_EVENT = "Could not create user updated event";
    public static final String COULD_NOT_CREATE_ACCOUNT_CREATED_EVENT = "Could not create account created event";
    public static final String COULD_NOT_CREATE_ACCOUNT_UPDATED_EVENT = "Could not create account updated event";
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
    public static final String SWAGGER_UI_PATH = "/swagger-ui.html";
    public static final String SWAGGER_UI_RESOURCES_PATH = "/swagger-ui/**";
    public static final String API_DOCS_PATH = "/v3/api-docs/**";
    public static final String API_ENDPOINT_AUTH_SIGNUP = "/api/auth/signup";
    public static final String API_ENDPOINT_AUTH_LOGIN = "/api/auth/login";
    public static final String API_ENDPOINT_ACTUATOR_HEALTH = "/actuator/health";
    public static final String API_ENDPOINT_ACTUATOR_METRICS = "/actuator/metrics";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String JWT_USER_ID_CLAIM = "userId";
    public static final String JWT_EMAIL_CLAIM = "email";
    public static final String JWT_ROLES_CLAIM = "roles";
    public static final String BEARER_AUTH = "bearerAuth";
    public static final String SCHEME_BEARER = "bearer";
    public static final String BEARER_FORMAT_JWT = "JWT";

}
