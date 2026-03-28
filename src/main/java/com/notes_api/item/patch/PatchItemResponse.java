package com.notes_api.item.patch;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ToString
public class PatchItemResponse {

    private UUID id;

    private String title;

    private String content;

    private Long version;

    private LocalDateTime updatedAt;

}
