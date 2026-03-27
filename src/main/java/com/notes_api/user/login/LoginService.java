package com.notes_api.user.login;

import com.notes_api.security.jtw.Token;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthService authService;

    @Autowired
    public LoginService(AuthService authService) {
        this.authService = authService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        Token token = authService.loginAttempt(request.getLogin(), request.getPassword());

        return LoginResponse.builder()
                .token(token.getTokenValue())
                .expiresAt(token.getExpiresAt())
                .build();
    }

}