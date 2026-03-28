package com.notes_api.item.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class PostItemRequest {

    @NotBlank(message = "title can't be empty")
    @Size(min = 1, max = 255, message = "title length requirements: 1-225")
    private String title;

    private String content;

}