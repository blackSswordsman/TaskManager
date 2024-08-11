package com.example.taskmanager.controller.dto;

import com.example.taskmanager.dao.Priority;
import com.example.taskmanager.dao.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на создание или обновление задачи")
public class TaskRequest {

    @NotNull(message = "task's header can't be empty")
    @NotBlank(message = "task's header can't be empty")
    @Schema(description = "Заголовок задачи", example = "Implement authentication")
    private String header;

    @NotBlank(message = "task's description can't be empty")
    @Schema(description = "Описание задачи", example = "Implement user authentication using Spring Security")
    private String description;

    @Email(message = "assignee must be in correct email form")
    @Schema(description = "Email исполнителя задачи", example = "assignee@example.com")
    private String assignee;

    @NotNull(message = "task's priority can't be empty")
    @Schema(description = "Приоритет задачи", example = "HIGH")
    private Priority priority;

    @NotNull(message = "task's status can't be empty")
    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private Status status;

}
