package com.payment.ingestor.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(

            @Value(JWT_SECRET)
            String secret,

            @Value(JWT_EXPIRATION_TIME)
            long expirationMs) {

        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(AppUserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(JWT_USER_ID_CLAIM, userDetails.getUserId().toString())
                .claim(JWT_EMAIL_CLAIM, userDetails.getEmail())
                .claim(JWT_ROLES_CLAIM, userDetails.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        String userId = extractAllClaims(token).get(JWT_USER_ID_CLAIM, String.class);
        return UUID.fromString(userId);
    }

    public boolean isTokenValid(String token, AppUserDetails userDetails) {
        String username = extractUsername(token);
        return username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

}
