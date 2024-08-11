package com.example.taskmanager.controller.dto;

import com.example.taskmanager.controller.ApplicationController;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.exception.ApplicationException;
import com.example.taskmanager.exception.NotAuthorizedException;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UserEmailException;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings(value = "unused")
@WebMvcTest(controllers = ApplicationController.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskServiceImpl taskService;

    @MockBean
    private Function<Task, TaskResponse> taskMapper;

    @Test
    void given_TaskNotFoundException_when_GetTaskById_then_ReturnsNotFoundMessage() throws Exception {
        // Мокируем выбрасывание TaskNotFoundException в taskService.getTaskById
        doThrow(new TaskNotFoundException()).when(taskService).getTaskById(anyLong());

        // Выполняем GET-запрос для получения задачи с ID 1
        mockMvc.perform(get("/api/tasks/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Task with this id does not exist"));
    }

    @Test
    void given_NotAuthorizedException_when_UpdateTask_then_ReturnsForbidden() throws Exception {

        doThrow(new NotAuthorizedException()).when(taskService).updateTask(any(Long.class), any(TaskRequest.class));

        String validTaskRequestJson = "{\"header\": \"Updated Task\", \"description\": \"Updated Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTaskRequestJson))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"message\":\"You do not have rights to the source\"}"));
    }


    @Test
    void given_UserEmailException_when_UpdateTask_then_ReturnsForbidden() throws Exception {

        doThrow(new UserEmailException()).when(taskService).updateTask(any(Long.class), any(TaskRequest.class));

        String validTaskRequestJson = "{\"header\": \"Updated Task\", \"description\": \"Updated Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTaskRequestJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"message\":\"Could not get user's email\"}"));
    }

    @Test
    void given_GenericApplicationException_when_UpdateTask_then_ReturnsInternalServerError() throws Exception {
        // Мокируем выбрасывание ApplicationException в taskService.updateTask
        doThrow(new ApplicationException("An unexpected error occurred")).when(taskService).updateTask(any(Long.class), any(TaskRequest.class));

        String validTaskRequestJson = "{\"header\": \"Updated Task\", \"description\": \"Updated Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTaskRequestJson))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("{\"message\":\"An unexpected error occurred\"}"));
    }

}