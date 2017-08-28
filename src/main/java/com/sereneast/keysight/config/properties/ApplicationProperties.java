package com.sereneast.keysight.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@ConfigurationProperties(prefix="keysight")
@Validated
public class ApplicationProperties {

    @NotNull
    private String accountQuery;

    public String getAccountQuery() {
        return accountQuery;
    }

    public void setAccountQuery(String accountQuery) {
        this.accountQuery = accountQuery;
    }

    @Configuration
    @PropertySource("classpath:job-account.properties")
    @ConfigurationProperties
    public static class JobPropertiesAccount extends JobProperties {
    }

    public static class JobProperties {
        private String jobName;
        private String cron;
        private HashMap<String,String> mapping;

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public HashMap<String, String> getMapping() {
            return mapping;
        }

        public void setMapping(HashMap<String, String> mapping) {
            this.mapping = mapping;
        }
    }
}
