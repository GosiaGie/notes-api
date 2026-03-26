package com.notes_api.user.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RegisterRequest {

    @NotBlank(message = "login can't be empty")
    @Size(min = 3, max = 64, message = "login length requirements: 3-64")
    private String login;

    @NotBlank(message = "password can't be empty")
    @Size(min = 8, max = 72, message = "password length requirements: 8-72")
    private String password;

}
