package com.notes_api.security.jtw;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties("security.jwt")
@Configuration
public class JwtProperties {

    private String secretKey;
    private int expirationMinutes;

}