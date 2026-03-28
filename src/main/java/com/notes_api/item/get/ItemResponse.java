package com.notes_api.item.get;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class ItemResponse {

    private UUID id;

    private String title;

    private String content;

    private Long version;

    private UUID ownerId;

    private String myRole;

    private LocalDateTime updatedAt;

}
