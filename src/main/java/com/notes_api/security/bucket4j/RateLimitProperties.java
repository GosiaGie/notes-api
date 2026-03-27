package com.notes_api.security.bucket4j;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("security.rate")
@Configuration
public class RateLimitProperties {

    private int capacity;
    private int refillMinutes;
}
