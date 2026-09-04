package com.payment.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PaymentIngestorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentIngestorApplication.class, args);
    }

}
