package com.notes_api.item.share;

import com.notes_api.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class ShareItemRequest {

    private UUID userId;
    private Role role;

}