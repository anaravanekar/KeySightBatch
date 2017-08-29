package com.sereneast.keysight;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeySightBatchApplication {

	public static void main(String[] args) throws JsonProcessingException {
		SpringApplication.run(KeySightBatchApplication.class, args);
	}
}