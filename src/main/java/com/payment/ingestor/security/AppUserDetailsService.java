package com.payment.ingestor.security;


import com.payment.ingestor.entity.User;
import com.payment.ingestor.exception.InvalidCredentialsException;
import com.payment.ingestor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.payment.ingestor.constant.PaymentIngestorConstants.INVALID_CREDENTIALS_EMAIL_OR_PASSWORD;
import static com.payment.ingestor.constant.PaymentIngestorConstants.INVALID_CREDENTIALS_FIELD;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                INVALID_CREDENTIALS_FIELD,
                                INVALID_CREDENTIALS_EMAIL_OR_PASSWORD
                        )
                );

        return new AppUserDetails(user);
    }
    
}
