package com.payment.ingestor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Configuration
public class JwtKeyConfig {

    @Bean
    public PrivateKey jwtPrivateKey(

            @Value(JWT_PRIVATE_KEY)
            String encodedKey
    ) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            return KeyFactory.getInstance(JWT_SIGNATURE_ALGORITHM_RSA).generatePrivate(keySpec);
        } catch (Exception ex) {
            throw new IllegalStateException(JWT_PRIVATE_KEY_LOAD_ERROR, ex);
        }
    }

    @Bean
    public PublicKey jwtPublicKey(

            @Value(JWT_PUBLIC_KEY)
            String encodedKey
    ) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

            return KeyFactory.getInstance(JWT_SIGNATURE_ALGORITHM_RSA).generatePublic(keySpec);
        } catch (Exception ex) {
            throw new IllegalStateException(JWT_PUBLIC_KEY_LOAD_ERROR, ex);
        }
    }

}

