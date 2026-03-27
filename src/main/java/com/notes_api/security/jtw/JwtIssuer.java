package com.notes_api.security.jtw;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.notes_api.user.register.datetime.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
@ConfigurationProperties(prefix = "security.jwt")
public class JwtIssuer {

    private final JwtProperties jwtProperties;

    private final DateTime dateTime;

    @Autowired
    public JwtIssuer(JwtProperties jwtProperties, DateTime dateTime) {
        this.jwtProperties = jwtProperties;
        this.dateTime = dateTime;
    }

    public String issue(UUID id, String email, List<String> roles) {
        return JWT.create()
                .withSubject(String.valueOf(id))
                .withExpiresAt(dateTime.getDateTime().plus(Duration.of(jwtProperties.getExpirationMinutes(),
                        ChronoUnit.MINUTES))
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                .withClaim("email", email)
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }

}