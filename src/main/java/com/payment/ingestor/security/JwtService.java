package com.payment.ingestor.security;

import com.payment.ingestor.exception.AppIllegalArgumentException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long expirationMs;
    private final String issuer;
    private final List<String> audiences;

    public JwtService(

            PrivateKey jwtPrivateKey,

            PublicKey jwtPublicKey,

            @Value(JWT_EXPIRATION_MS)
            long expirationMs,

            @Value(JWT_ISSUER)
            String issuer,

            @Value(JWT_AUDIENCES)
            List<String> audiences
    ) {
        this.privateKey = jwtPrivateKey;
        this.publicKey = jwtPublicKey;
        this.expirationMs = expirationMs;
        this.issuer = issuer;
        this.audiences = List.copyOf(audiences);

        if (this.audiences.isEmpty()) {
            throw new AppIllegalArgumentException(
                    JWT_AUDIENCE_FIELD,
                    JWT_AUDIENCE_REQUIRED
            );
        }
    }

    public String generateToken(AppUserDetails userDetails) {

        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .claim(JWT_AUD_CLAIM, audiences)
                .claim(JWT_USER_ID_CLAIM, userDetails.getUserId().toString())
                .claim(JWT_EMAIL_CLAIM, userDetails.getEmail())
                .claim(JWT_ROLES_CLAIM, userDetails.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(privateKey, Jwts.SIG.RS256)
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
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        return username != null
                && username.equalsIgnoreCase(userDetails.getUsername())
                && claims.getExpiration() != null
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        validateIssuerAndAudience(claims);
        return claims;
    }

    private void validateIssuerAndAudience(Claims claims) {

        if (!issuer.equals(claims.getIssuer())) {
            throw new AppIllegalArgumentException(
                    JWT_ISSUER_FIELD,
                    JWT_INVALID_ISSUER
            );
        }

        Object audienceClaim = claims.get(JWT_AUD_CLAIM);
        boolean audienceMatches = false;

        if (audienceClaim instanceof String audience) {
            audienceMatches = audiences.contains(audience);
        } else if (audienceClaim instanceof Collection<?> audienceValues) {
            audienceMatches = audienceValues.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .anyMatch(audiences::contains);
        }

        if (!audienceMatches) {
            throw new AppIllegalArgumentException(
                    JWT_AUDIENCE_FIELD,
                    JWT_INVALID_AUDIENCE
            );
        }
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

}
