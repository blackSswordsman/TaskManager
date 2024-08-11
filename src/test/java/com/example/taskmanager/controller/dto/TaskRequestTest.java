package com.example.taskmanager.controller.dto;

import com.example.taskmanager.controller.ApplicationController;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings(value = "unused")
@WebMvcTest(ApplicationController.class)
class TaskRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskServiceImpl taskService;

    @MockBean
    private Function<Task, TaskResponse> taskMapper;

    @Test
    void given_EmptyHeader_when_UpdateTask_then_ReturnsBadRequest() throws Exception {
        String invalidTaskRequestJson = "{\"header\": \"\", \"description\": \"Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.header").value("task's header can't be empty"));
    }

    @Test
    void given_EmptyDescription_when_UpdateTask_then_ReturnsBadRequest() throws Exception {
        String invalidTaskRequestJson = "{\"header\": \"Header\", \"description\": \"\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("task's description can't be empty"));
    }

    @Test
    void given_InvalidEmail_when_UpdateTask_then_ReturnsBadRequest() throws Exception {
        String invalidTaskRequestJson = "{\"header\": \"Header\", \"description\": \"Description\", \"assignee\": \"invalid-email\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.assignee").value("assignee must be in correct email form"));
    }

    @Test
    void given_NullPriority_when_UpdateTask_then_ReturnsBadRequest() throws Exception {
        String invalidTaskRequestJson = "{\"header\": \"Header\", \"description\": \"Description\", \"assignee\": \"assignee@example.com\", \"priority\": null, \"status\": \"IN_PROGRESS\"}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.priority").value("task's priority can't be empty"));
    }

    @Test
    void given_NullStatus_when_UpdateTask_then_ReturnsBadRequest() throws Exception {
        String invalidTaskRequestJson = "{\"header\": \"Header\", \"description\": \"Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": null}";

        mockMvc.perform(put("/api/tasks/{id}/update_task", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidTaskRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("task's status can't be empty"));
    }
}
