package com.example.taskmanager.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(description = "Запрос на создание комментария")
public class CommentRequest {
    @NotBlank(message = "comment text can't be empty")
    @Schema(description = "Текст комментария", example = "Comment text")
    private String description;

}
