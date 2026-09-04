package com.payment.ingestor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(JWT_TOKEN_STARTS_WITH_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(JWT_TOKEN_VALUE_BEGIN_INDEX_SEVEN);
        try {
            String email = jwtService.extractUsername(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AppUserDetails userDetails = (AppUserDetails) userDetailsService.loadUserByUsername(email);
                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ignored) {
            /*
             * Invalid JWT. Leave SecurityContext unauthenticated.
             * Spring Security will subsequently return 401 for protected endpoints.
             */
        }
        filterChain.doFilter(request, response);
    }
    
}
