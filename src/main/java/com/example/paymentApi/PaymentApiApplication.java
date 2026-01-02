package com.example.paymentApi;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
		info =
		@Info(
				title = "coinBridge stablecoin webapi",
				description = "coinBridge stablecoin webapi Documentation",
				version = "1.0",
				contact = @Contact(name = "Frank")))

public class PaymentApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApiApplication.class, args);
	}

}
