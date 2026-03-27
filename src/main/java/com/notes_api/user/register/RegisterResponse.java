package com.notes_api.user.register;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class RegisterResponse {

    private String id;
    private String login;
    private LocalDateTime createdAt;

}