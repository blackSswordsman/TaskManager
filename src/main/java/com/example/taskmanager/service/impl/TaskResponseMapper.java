package com.example.taskmanager.service.impl;

import com.example.taskmanager.controller.dto.TaskResponse;
import com.example.taskmanager.dao.Task;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Класс для маппинга объкета типа Task из Entity слоя в объекта типа TaskResponse (dto) для ответа клиенту
 */
@Component
public class TaskResponseMapper implements Function<Task, TaskResponse> {
    /**
     * Метод применяет функцию для маппинга объкета типа Task из Entity слоя в объекта типа TaskResponse (dto) для ответа клиенту
     * @param task the function argument
     * @return объект типа TaskResponse
     */
    @Override
    public TaskResponse apply(Task task) {
        return new TaskResponse()
                .setId(task.getId())
                .setDescription(task.getDescription())
                .setPriority(task.getPriority())
                .setStatus(task.getStatus())
                .setAssignee(task.getAssignee())
                .setHeader(task.getHeader())
                .setCreator(task.getCreator())
                .setCreatedAt(task.getCreatedAt())
                .setComments(task.getComments().stream().map(comment ->
                        new TaskResponse.Comment().setAuthor(comment.getAuthor())
                                .setBody(comment.getDescription())
                                .setId(comment.getId())
                                .setCreatedAt(comment.getCreatedAt())).toList());

    }
}
