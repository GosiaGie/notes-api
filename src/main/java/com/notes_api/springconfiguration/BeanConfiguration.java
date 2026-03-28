package com.notes_api.springconfiguration;

import com.notes_api.security.bucket4j.RateLimitProperties;
import com.notes_api.security.bucket4j.RateLimitingFilter;
import com.notes_api.security.jtw.*;
import com.notes_api.user.register.datetime.DateTimeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfiguration {

    @Bean
    public JwtIssuer jwtIssuer() { return new JwtIssuer(properties(), dateTime()); };

    @Bean
    public JwtProperties properties() {
        return new JwtProperties();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder(properties());
    }

    @Bean
    public JwtToPrincipalConverter jwtToPrincipalConverter() {
        return new JwtToPrincipalConverter();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtDecoder(), jwtToPrincipalConverter());
    }

    @Bean
    public RateLimitProperties rateLimitProperties() {
        return new RateLimitProperties();
    }

    @Bean
    public RateLimitingFilter rateLimitingFilter() {
        return new RateLimitingFilter(rateLimitProperties());
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public DateTimeImpl dateTime() { return new DateTimeImpl(clock()) ;}

}