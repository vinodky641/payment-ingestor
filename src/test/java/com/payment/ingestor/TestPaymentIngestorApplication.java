package com.payment.ingestor;

import org.springframework.boot.SpringApplication;

public class TestPaymentIngestorApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentIngestorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
