package com.notes_api.springconfiguration;

import com.notes_api.security.JwtAuthenticationFilter;
import com.notes_api.security.JwtDecoder;
import com.notes_api.security.JwtProperties;
import com.notes_api.security.JwtToPrincipalConverter;
import com.notes_api.user.register.datetime.DateTimeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class BeanConfiguration {

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
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public DateTimeImpl dateTime() {
        return new DateTimeImpl(clock());
    }

}