package com.notes_api.user.login;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LoginRequest {

    @NotBlank(message = "login can't be empty")
    private String login;

    @NotBlank(message = "password can't be empty")
    private String password;

}