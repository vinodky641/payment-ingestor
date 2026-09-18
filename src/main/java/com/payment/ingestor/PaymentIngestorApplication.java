package com.payment.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("com.payment.ingestor.config")
public class PaymentIngestorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentIngestorApplication.class, args);
    }

}
