package com.sereneast.keysight;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("keysight")
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
}
