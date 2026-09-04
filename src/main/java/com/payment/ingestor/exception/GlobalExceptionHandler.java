package com.payment.ingestor.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //App user's input validation exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        List<Violation> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Violation(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        VALIDATION_FAILED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    //App user's input constraint Violation exception handler
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        List<Violation> violations = ex.getConstraintViolations()
                .stream()
                .map(error -> new Violation(
                        error.getPropertyPath().toString(),
                        error.getMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        VALIDATION_FAILED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // App user's duplicate finance account exception handler
    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateAccountException(
            DuplicateAccountException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        DUPLICATE_ACCOUNT_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // App user's email already registered exception handler
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        EMAIL_ALREADY_EXISTS_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // App user's payment request's Idempotency key conflict exception handler
    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleIdempotencyConflictException(
            IdempotencyConflictException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.CONFLICT.value(),
                        IDEMPOTENCY_KEY_CONFLICT_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // App user invalid credentials exception handler
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        INVALID_CREDENTIALS_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // Unauthorized user exception handler
    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedUserException(
            UnauthorizedUserException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        UNAUTHORIZED_USER_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }


    // App user account disabled exception handler
    @ExceptionHandler(UserAccountDisabledException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAccountDisabledException(
            UserAccountDisabledException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        USER_ACCOUNT_DISABLED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // App user account locked exception handler
    @ExceptionHandler(UserAccountLockedException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAccountLockedException(
            UserAccountLockedException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.FORBIDDEN.value(),
                        USER_ACCOUNT_LOCKED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // Debit and Credit same account exception handler
    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidPaymentException(
            InvalidPaymentException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        VALIDATION_FAILED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // Debit or Credit account not found exception handler
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountNotFoundException(
            AccountNotFoundException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        ACCOUNT_NOT_FOUND_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // Debit or Credit account suspended exception handler
    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccountSuspendedException(
            AccountSuspendedException ex,
            HttpServletRequest request) {

        List<Violation> violations = List.of(new Violation(
                ex.getField(),
                ex.getMessage()
        ));

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        ACCOUNT_SUSPENDED_ERROR,
                        request.getRequestURI(),
                        violations
                ));
    }

    // General exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        INTERNAL_SERVER_ERROR,
                        request.getRequestURI(),
                        List.of()
                ));
    }

}
