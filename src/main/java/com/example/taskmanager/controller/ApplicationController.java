package com.example.taskmanager.controller;


import com.example.taskmanager.controller.dto.CommentRequest;
import com.example.taskmanager.controller.dto.PageResponse;
import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.controller.dto.TaskResponse;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

/**
 * REST контроллер для управления задачами.
 * Этот контроллер предоставляет методы для создания, обновления, удаления и получения задач,
 * а также для изменения статуса задачи, назначения исполнителя и добавления комментариев.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API для управления задачами")
public class ApplicationController {

    private final TaskServiceImpl taskService;
    private final Function<Task, TaskResponse> taskMapper;

    /**
     * Метод обрабатывает post запрос для создания новой задачи.
     *
     * @param task --экземпляр TaskRequest для создания задачи
     * @return {@link TaskResponse} с информацией о созданной задаче
     */
    @Operation(summary = "Создать задачу", description = "Создает новую задачу и возвращает её данные")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PostMapping("/create_task")
    public TaskResponse createTask(@Valid @RequestBody TaskRequest task) {
        return taskMapper.apply(taskService.createTask(task));
    }

    /**
     * Метод обрабатывает get запрос для получения всей информации о задаче по ее id.
     *
     * @param id --идентификатор задачи
     * @return {@link TaskResponse} с информацией о задаче
     */
    @Operation(summary = "Получить задачу по ID", description = "Возвращает данные задачи по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable @Min(value = 1, message = "id must be positive") Long id) {
        return taskMapper.apply(taskService.getTaskById(id));
    }

    /**
     * Метод обрабатывает get запрос для получения списка всех задач пользователя.
     *
     * @param email --email пользователя, для которого нужно получить список задач
     * @param page  --номер страницы для пагинации
     * @param size  --количество задач на странице
     * @return {@link PageResponse} с информацией о задачах
     */
    @Operation(summary = "Получить задачи пользователя", description = "Возвращает список задач пользователя с поддержкой пагинации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен", content = @Content(schema = @Schema(implementation = PageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса")
    })
    @GetMapping("/all_tasks")
    public PageResponse<TaskResponse> getTasks(
            @Parameter(description = "Email пользователя", required = true)
            @RequestParam @Email(message = "email must be in correct form") String email,
            @Parameter(description = "Номер страницы для пагинации", required = true)
            @RequestParam @Min(value = 0, message = "page must be minimum 0") int page,
            @Parameter(description = "Количество задач на странице", required = true)
            @RequestParam @Min(value = 1, message = "size must be positive") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaskResponse> map = taskService.getAllTasksByUser(email, pageable)
                .map(taskMapper);
        return PageResponse.valueOf(map);
    }

    /**
     * Метод обрабатывает put запрос для редактирования существующей задачи.
     *
     * @param id   --идентификатор задачи
     * @param task --экземпляр TaskRequest для редактирования задачи
     * @return {@link TaskResponse} с информацией об обновленной задаче
     */
    @Operation(summary = "Обновить задачу", description = "Обновляет данные существующей задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PutMapping("/{id}/update_task")
    public TaskResponse updateTask(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable @Min(value = 1, message = "id must be positive") Long id,
            @Valid @RequestBody TaskRequest task) {
        return taskMapper.apply(taskService.updateTask(id, task));
    }

    /**
     * Метод обрабатывает delete запрос для удаления конкретной задачи.
     *
     * @param id --идентификатор задачи
     * @return сообщение об успешном удалении конкретной задачи
     */
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{id}/delete_task")
    public String deleteTask(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable @Min(value = 1, message = "id must be positive") Long id) {
        taskService.deleteTask(id);
        return "Task with id " + id + " has been deleted";
    }

    /**
     * Метод обрабатывает patch запрос для изменения статуса конкретной задачи.
     *
     * @param id     --идентификатор задачи
     * @param status --новый статус задачи
     * @return {@link TaskResponse} с информацией об обновленной задаче
     */
    @Operation(summary = "Изменить статус задачи", description = "Изменяет статус задачи по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлён", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PatchMapping("/{id}/change_status")
    public TaskResponse changeStatus(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable @Min(value = 1, message = "id must be positive") Long id,
            @Parameter(description = "Новый статус задачи", required = true)
            @NotNull(message = "status can't be null") @RequestParam Status status) {
        return taskMapper.apply(taskService.changeStatus(id, status));
    }

    /**
     * Метод обрабатывает patch запрос для назначения исполнителя задачи.
     *
     * @param id       --идентификатор задачи
     * @param assignee --email исполнителя задачи
     * @return {@link TaskResponse} с информацией об обновленной задаче
     */
    @Operation(summary = "Назначить исполнителя задачи", description = "Назначает исполнителя для задачи по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель успешно назначен", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PatchMapping("{id}/set_assignee")
    public TaskResponse setAssignee(
            @Parameter(description = "Идентификатор задачи", required = true)
        @PathVariable Long id,
            @Parameter(description = "Email исполнителя задачи", required = true)
            @Email(message = "assignee must be in correct email form") @RequestParam String assignee) {
        return taskMapper.apply(taskService.setAssignee(id, assignee));
    }

    /**
     * Метод обрабатывает post запрос для добавления комментария к задаче.
     *
     * @param id      --идентификатор задачи
     * @param comment --экземпляр комментария
     * @return {@link TaskResponse} с информацией об обновленной задаче
     */
    @Operation(summary = "Добавить комментарий к задаче", description = "Добавляет комментарий к задаче по её идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    @PostMapping("{id}/add_comment")
    public TaskResponse addComment(
            @Parameter(description = "Идентификатор задачи", required = true)
            @PathVariable @Min(value = 1, message = "id must be positive") Long id,
            @Valid @RequestBody CommentRequest comment) {
        return taskMapper.apply(taskService.addComment(id, comment.getDescription()));
    }
}
