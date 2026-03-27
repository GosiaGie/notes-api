package com.notes_api.user.login;

import com.notes_api.security.jtw.JwtIssuer;
import com.notes_api.security.jtw.JwtProperties;
import com.notes_api.security.jtw.Token;
import com.notes_api.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConfigurationProperties(prefix = "security.jwt")
public class AuthService {

    private final JwtIssuer jwtIssuer;
    private final JwtProperties jwtProperties;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(JwtIssuer jwtIssuer, JwtProperties jwtProperties, AuthenticationManager authenticationManager) {
        this.jwtIssuer = jwtIssuer;
        this.jwtProperties = jwtProperties;
        this.authenticationManager = authenticationManager;
    }

    public Token loginAttempt(String email, String password) {

        var authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        
        //setting this authentication in Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var principal = (UserPrincipal) authentication.getPrincipal();
        var roles = principal.getAuthorities().stream().
                map(GrantedAuthority::getAuthority)
                .toList();

        return Token.builder()
                .tokenValue(jwtIssuer.issue(principal.getId(), principal.getLogin(), roles))
                .expiresAt(jwtProperties.getExpirationMinutes())
                .build();

    }

    public UUID getAuthenticatedUserID() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserPrincipal) authentication.getPrincipal()).getId();

    }

}