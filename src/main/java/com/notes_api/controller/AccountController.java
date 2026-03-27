package com.notes_api.controller;

import com.notes_api.user.login.LoginRequest;
import com.notes_api.user.login.LoginResponse;
import com.notes_api.user.login.LoginService;
import com.notes_api.user.register.RegisterRequest;
import com.notes_api.user.register.RegisterResponse;
import com.notes_api.user.register.RegisterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final RegisterService registerService;
    private final LoginService loginService;

    @Autowired
    public AccountController(RegisterService registerService, LoginService loginService) {
        this.registerService = registerService;
        this.loginService = loginService;
    }

    @PostMapping("auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

       return registerService.register(request);

    }

    @PostMapping("auth/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        return loginService.login(request);

    }

}