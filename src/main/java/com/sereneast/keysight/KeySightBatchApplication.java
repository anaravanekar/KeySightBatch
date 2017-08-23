package com.sereneast.keysight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class KeySightBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(KeySightBatchApplication.class, args);
	}
}
