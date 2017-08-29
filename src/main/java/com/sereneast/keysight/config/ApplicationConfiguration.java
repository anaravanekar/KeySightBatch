package com.sereneast.keysight.config;

import com.sereneast.keysight.config.properties.AccountJobProperties;
import com.sereneast.keysight.config.properties.AddressJobProperties;
import com.sereneast.keysight.config.properties.ApplicationProperties;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@EnableConfigurationProperties({AccountJobProperties.class, AddressJobProperties.class, ApplicationProperties.class})
public class ApplicationConfiguration {

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    public NamedParameterJdbcTemplate oracleDbNamedParameterJdbcTemplate(DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
