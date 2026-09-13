package com.payment.ingestor.service;

import com.payment.ingestor.dto.user.UpdateUserRequest;
import com.payment.ingestor.dto.user.UserResponse;
import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.UnauthorizedUserException;
import com.payment.ingestor.repository.UserRepository;
import com.payment.ingestor.security.AppUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.payment.ingestor.constant.PaymentIngestorConstants.EMAIL_FIELD;
import static com.payment.ingestor.constant.PaymentIngestorConstants.UNAUTHORIZED_USER_NOT_PERMITTED;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserUpdatedOutboxService userUpdatedOutboxService;

    @Transactional
    public UserResponse updateProfile(
            UpdateUserRequest request,
            AppUserDetails userDetails
    ) {

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new UnauthorizedUserException(
                                EMAIL_FIELD,
                                UNAUTHORIZED_USER_NOT_PERMITTED
                        )
                );

        user.updateProfile(
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                request.displayName()
        );

        userRepository.save(user);
        userUpdatedOutboxService.createOutboxEvent(user);
        return UserResponse.from(user);
    }

}
