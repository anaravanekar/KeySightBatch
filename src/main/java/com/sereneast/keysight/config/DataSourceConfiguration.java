package com.sereneast.keysight.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

//@Configuration
public class DataSourceConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name="oracleDbDataSource")
    @Primary
    public DataSource jobDataSource()  {
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setDriverClassName(environment.getProperty("keysight.datasource.oracle.driver-class-name"));
        ds.setUrl(environment.getProperty("keysight.datasource.oracle.url"));
        ds.setUsername(environment.getProperty("keysight.datasource.oracle.username"));
        ds.setPassword(environment.getProperty("keysight.datasource.oracle.password"));
        ds.setInitialSize(2);
        ds.setMaxActive(10);
        ds.setMaxIdle(5);
        ds.setMinIdle(2);
        ds.setMaxWait(2000);
        return ds;
    }

    @Bean(name = "oracleDbNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate namedJdbcTemplate(@Qualifier("oracleDbDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
