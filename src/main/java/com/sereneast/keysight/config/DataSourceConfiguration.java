package com.sereneast.keysight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Autowired
    private Environment environment;

    @ConfigurationProperties(prefix = "keysight.datasource.oracle")
    @Bean(name="oracleDbDataSource")
    public DataSource jobDataSource()  {
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(environment.getProperty("spring.job.datasource.driverClassName"));
        ds.setUrl(environment.getProperty("spring.job.datasource.url"));
        ds.setUsername(environment.getProperty("spring.job.datasource.username"));
        ds.setPassword(environment.getProperty("spring.job.datasource.password"));
        ds.setInitialSize(2);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);
        ds.setMaxWait(2000);
        return ds;
    }

    @Bean(name = "oracleDbNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedJdbcTemplate(@Qualifier("jobDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
