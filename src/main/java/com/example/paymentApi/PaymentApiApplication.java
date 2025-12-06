package com.example.paymentApi;


import com.example.paymentApi.shared.utility.EntitySecretCipherTextUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.PublicKey;

@SpringBootApplication
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
