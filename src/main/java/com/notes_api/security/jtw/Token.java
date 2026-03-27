package com.notes_api.security.jtw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Token {

    private String tokenValue;
    private int expiresAt;

}