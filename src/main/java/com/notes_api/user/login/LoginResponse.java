package com.notes_api.user.login;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class LoginResponse {

    String token;
    Integer expiresAt;

}