package com.notes_api.item.patch;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PatchItemRequest {

    private String title;

    private String content;

    @NotNull(message = "version can't be empty")
    private Long version;

}