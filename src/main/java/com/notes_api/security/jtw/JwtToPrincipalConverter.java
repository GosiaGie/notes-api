package com.notes_api.security.jtw;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.notes_api.security.UserPrincipal;

import java.util.UUID;

public class JwtToPrincipalConverter {

    public UserPrincipal convert(DecodedJWT decodedJWT){
        return UserPrincipal.builder()
                .id(UUID.fromString(decodedJWT.getSubject()))
                .login(decodedJWT.getClaim("login").asString())
                .build();
    }
}