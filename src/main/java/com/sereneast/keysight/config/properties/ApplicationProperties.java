package com.sereneast.keysight.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix="keysight")
public class ApplicationProperties {
    private Map<String,String> queries = new HashMap<String,String>();

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }
}

