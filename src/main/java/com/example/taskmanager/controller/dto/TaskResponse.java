package com.example.taskmanager.controller.dto;

import com.example.taskmanager.dao.Priority;
import com.example.taskmanager.dao.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(description = "Ответ с информацией о задаче")
public class TaskResponse {

    @Schema(description = "Идентификатор задачи", example = "1")
    private Long id;

    @Schema(description = "Заголовок задачи", example = "Implement authentication")
    private String header;

    @Schema(description = "Описание задачи", example = "Implement user authentication using Spring Security")
    private String description;

    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private Status status;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private Priority priority;

    @Schema(description = "Создатель задачи", example = "john.doe@example.com")
    private String creator;

    @Schema(description = "Исполнитель задачи", example = "jane.doe@example.com")
    private String assignee;

    @Schema(description = "Дата и время создания задачи", example = "2024-08-09T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "Список комментариев к задаче")
    private List<Comment> comments;

    @Data
    @Accessors(chain = true)
    @Schema(description = "Комментарий к задаче")
    public static class Comment {

        @Schema(description = "Идентификатор комментария", example = "1")
        private Long id;

        @Schema(description = "Текст комментария", example = "This is a comment on the task.")
        private String body;

        @Schema(description = "Автор комментария", example = "jane.doe@example.com")
        private String author;

        @Schema(description = "Дата и время создания комментария", example = "2024-08-09T14:56:00")
        private LocalDateTime createdAt;
    }
}
