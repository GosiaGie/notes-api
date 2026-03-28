package com.notes_api.item.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ItemHistory {
    private Integer revision;

    private String revisionType;

    private LocalDateTime timestamp;

    private String changedBy;

    private String title;

    private String content;
}
