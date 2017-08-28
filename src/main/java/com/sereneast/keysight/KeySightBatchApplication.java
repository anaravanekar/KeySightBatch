package com.sereneast.keysight;

import com.sereneast.keysight.config.properties.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class KeySightBatchApplication {

	@Resource
	private ApplicationProperties applicationProperties;

	public static void main(String[] args) {
		SpringApplication.run(KeySightBatchApplication.class, args);
		applicationProperties.g
	}
}
