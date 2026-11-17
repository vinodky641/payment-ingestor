package com.payment.ingestor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.payment.ingestor.constant.PaymentIngestorConstants.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ingestorOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Payment Ingestor API")
                        .version("1.0.0")
                        .description("REST API for Payment Ingestion, user management, account management, and payment submission.")
                )
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER_AUTH,
                                        new SecurityScheme()
                                                .name(AUTHORIZATION_HEADER)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme(SCHEME_BEARER)
                                                .bearerFormat(BEARER_FORMAT_JWT)
                                )
                );
    }

}
