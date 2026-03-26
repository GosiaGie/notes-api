package com.notes_api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class UserPrincipalAuthenticationToken extends AbstractAuthenticationToken {

    private final UserPrincipal userPrincipal;

    @Autowired
    public UserPrincipalAuthenticationToken(UserPrincipal userPrincipal) {
        super(userPrincipal.getAuthorities());
        this.userPrincipal = userPrincipal;
        setAuthenticated(true); //sets token to authenticated
    }

    @Override
    public Object getCredentials() {return null;}

    @Override
    public UserPrincipal getPrincipal() {
        return userPrincipal;
    }
}