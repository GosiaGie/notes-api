package com.notes_api.security;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.UUID;

public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT decodedJWT){
        return UserPrincipal.builder()
                .userID(UUID.fromString(decodedJWT.getSubject()))
                .login(decodedJWT.getClaim("login").asString())
                .build();
    }
}