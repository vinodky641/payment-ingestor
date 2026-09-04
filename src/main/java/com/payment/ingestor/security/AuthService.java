package com.payment.ingestor.security;

import com.payment.ingestor.dto.auth.LoginRequest;
import com.payment.ingestor.dto.auth.LoginResponse;
import com.payment.ingestor.dto.auth.SignupRequest;
import com.payment.ingestor.dto.auth.SignupResponse;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.EmailAlreadyExistsException;
import com.payment.ingestor.exception.InvalidCredentialsException;
import com.payment.ingestor.exception.UserAccountDisabledException;
import com.payment.ingestor.exception.UserAccountLockedException;
import com.payment.ingestor.model.UserStatus;
import com.payment.ingestor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(
                    EMAIL_FIELD,
                    EMAIL_ALREADY_REGISTERED + email
            );
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phoneNumber(request.phoneNumber())
                .displayName(request.displayName())
                .status(UserStatus.ACTIVE)
                .failedLoginAttempts(0)
                .emailVerified(false)
                .build();

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new InvalidCredentialsException(
                        INVALID_CREDENTIALS_FIELD,
                        INVALID_CREDENTIALS_EMAIL_OR_PASSWORD
                )
        );

        checkAccountState(user);
        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            handleFailedLoginAttempts(user);
            throw new InvalidCredentialsException(
                    INVALID_CREDENTIALS_FIELD,
                    INVALID_CREDENTIALS_EMAIL_OR_PASSWORD
            );
        }

        handleSuccessfulLogin(user);
        AppUserDetails userDetails = new AppUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                TOKEN_TYPE_BEARER,
                jwtService.getExpirationSeconds(),
                user.getId(),
                user.getEmail()
        );
    }

    // Validates status before checking the password.
    // Auto-unlocks the account if the lock window has already expired.
    private void checkAccountState(User user) {
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new UserAccountDisabledException(
                    USER_ACCOUNT_FIELD,
                    USER_ACCOUNT_IS_DISABLED
            );
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            Instant lockedUntil = user.getLockedUntil();

            if (lockedUntil != null && lockedUntil.isAfter(Instant.now())) {
                throw new UserAccountLockedException(
                        USER_ACCOUNT_FIELD,
                        USER_ACCOUNT_IS_LOCKED + user.getEmail()
                );
            }

            // Lock window has expired -> auto-unlock and give the user a fresh start
            unlockAccount(user);
        }
    }

    private void handleFailedLoginAttempts(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        // Lock after max failed attempts.
        if (attempts >= USER_MAX_FAILED_ATTEMPTS) {
            user.setStatus(UserStatus.LOCKED);
            user.setLockedUntil(Instant.now().plus(USER_LOCK_DURATION));
        }

        userRepository.save(user);
    }

    private void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    private void unlockAccount(User user) {
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

}
