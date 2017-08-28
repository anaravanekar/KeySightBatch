package com.sereneast.keysight;

import com.sereneast.keysight.config.AccountBatchJobConfiguration;
import com.sereneast.keysight.config.DataSourceConfiguration;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties.class)
@Import({DataSourceConfiguration.class,AccountBatchJobConfiguration.class})
public class ApplicationConfiguration {
}
