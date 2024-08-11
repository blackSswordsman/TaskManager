package com.example.taskmanager.service.impl;

import com.example.taskmanager.controller.dto.TaskResponse;
import com.example.taskmanager.dao.Comment;
import com.example.taskmanager.dao.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.example.taskmanager.utils.TestHelper.createTaskWithComments;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskResponseMapperTest {

    private TaskResponseMapper taskResponseMapper;

    @BeforeEach
    void setUp() {
        taskResponseMapper = new TaskResponseMapper();
    }

    @Test
    void givenTask_whenApply_thenMapsToTaskResponseCorrectly() {
        // Arrange
        Task task = createTaskWithComments(1L);

        // Act
        TaskResponse taskResponse = taskResponseMapper.apply(task);

        // Assert
        assertEquals(task.getId(), taskResponse.getId());
        assertEquals(task.getDescription(), taskResponse.getDescription());
        assertEquals(task.getPriority(), taskResponse.getPriority());
        assertEquals(task.getStatus(), taskResponse.getStatus());
        assertEquals(task.getAssignee(), taskResponse.getAssignee());
        assertEquals(task.getHeader(), taskResponse.getHeader());
        assertEquals(task.getCreator(), taskResponse.getCreator());
        assertEquals(task.getCreatedAt(), taskResponse.getCreatedAt());

        // Проверяем, что комментарии также были маппированы правильно
        assertEquals(task.getComments().size(), taskResponse.getComments().size());
        for (int i = 0; i < task.getComments().size(); i++) {
            Comment originalComment = task.getComments().get(i);
            TaskResponse.Comment responseComment = taskResponse.getComments().get(i);

            assertEquals(originalComment.getId(), responseComment.getId());
            assertEquals(originalComment.getDescription(), responseComment.getBody());
            assertEquals(originalComment.getAuthor(), responseComment.getAuthor());
            assertEquals(originalComment.getCreatedAt(), responseComment.getCreatedAt());
        }
    }
}