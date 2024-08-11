package com.example.taskmanager.utils;

import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.controller.dto.TaskResponse;
import com.example.taskmanager.dao.Comment;
import com.example.taskmanager.dao.Priority;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;

import java.time.LocalDateTime;
import java.util.List;

public class TestHelper {
    public static Task createTaskWithComments(Long id) {
        Comment comment1 = new Comment()
                .setId(1L)
                .setDescription("First comment")
                .setAuthor("author1@example.com")
                .setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment comment2 = new Comment()
                .setId(2L)
                .setDescription("Second comment")
                .setAuthor("author2@example.com")
                .setCreatedAt(LocalDateTime.now().minusHours(10));
        return createTask(id)
                .setComments(List.of(comment1, comment2));
    }

    public static Task createTask(Long id) {
        return new Task()
                .setId(id)
                .setCreatedAt(LocalDateTime.now())
                .setCreator("test@example.com")
                .setAssignee("assignee@mail.com")
                .setDescription("description")
                .setHeader("header")
                .setPriority(Priority.HIGH)
                .setStatus(Status.IN_PROGRESS);
    }

    public static TaskRequest createTaskRequest() {
        return new TaskRequest()
                .setAssignee("assignee@mail.com")
                .setDescription("description")
                .setHeader("header")
                .setPriority(Priority.HIGH)
                .setStatus(Status.IN_PROGRESS);


    }

    public static TaskResponse createTaskResponse(Long id) {
        TaskResponse.Comment comment1 = new TaskResponse.Comment()
                .setId(1L)
                .setBody("First comment")
                .setAuthor("author1@example.com")
                .setCreatedAt(LocalDateTime.now().minusDays(1));

        TaskResponse.Comment comment2 = new TaskResponse.Comment()
                .setId(2L)
                .setBody("Second comment")
                .setAuthor("author2@example.com")
                .setCreatedAt(LocalDateTime.now().minusHours(10));
        return new TaskResponse()
                .setId(id)
                .setCreator("test@example.com")
                .setHeader("New Task")
                .setDescription("Description")
                .setAssignee("assignee@example.com")
                .setPriority(Priority.HIGH)
                .setStatus(Status.IN_PROGRESS)
                .setComments(List.of(comment1, comment2));
    }

}
