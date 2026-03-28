package com.notes_api.item.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostItemResponse {

    private UUID id;

    private String title;

    private String content;

    private Long version;

    private UUID ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
