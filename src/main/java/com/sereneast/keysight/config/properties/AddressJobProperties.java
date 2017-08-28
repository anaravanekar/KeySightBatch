package com.sereneast.keysight.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="keysight.job.address")
public class AddressJobProperties extends JobProperties {
}
