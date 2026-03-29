package com.notes_api.item.share;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class ShareItemResponse {

    private UUID itemID;

    private UUID userID;

    private String role;

    private LocalDateTime grantedAt;

}