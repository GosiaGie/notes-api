package com.notes_api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("security.jwt")
public class JwtProperties {

    private String secretKey;

}