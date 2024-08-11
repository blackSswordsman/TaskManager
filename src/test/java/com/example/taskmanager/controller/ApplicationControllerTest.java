package com.example.taskmanager.controller;

import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.controller.dto.TaskResponse;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Function;

import static com.example.taskmanager.utils.TestHelper.createTask;
import static com.example.taskmanager.utils.TestHelper.createTaskResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class ApplicationControllerTest {
    @MockBean
    TaskServiceImpl taskService;
    @MockBean
    Function<Task, TaskResponse> taskMapper;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
    }

    @Test
    void given_TaskId_when_postRequestToCreateTask_then_correctResponse() throws Exception {
        // given
        TaskResponse taskResponse = createTaskResponse(1L);

        // when then
        when(taskMapper.apply(any())).thenReturn(taskResponse);

        mockMvc.perform(post("/api/tasks/create_task")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"header\": \"New Task\", \"description\": \"Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header").value("New Task"))
                .andExpect(jsonPath("$.description").value("Description"));
        verify(taskService, times(1)).createTask(any());
    }

    @Test
    void given_TaskId_when_GetRequestToGetTaskById_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        Task task = createTask(id);
        TaskResponse taskResponse = createTaskResponse(id);
        // when then
        when(taskService.getTaskById(id)).thenReturn(task);
        when(taskMapper.apply(any(Task.class))).thenReturn(taskResponse);
        mockMvc.perform(get("/api/tasks/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void given_filterParameters_when_getRequestToGetTasks_then_correctResponse() throws Exception {
        // given
        when(taskService.getAllTasksByUser(any(), any())).thenReturn(Page.empty());
        // when then
        mockMvc.perform(get("/api/tasks/all_tasks")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .param("email", "test@example.com")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void given_TaskIdAndTaskRequest_when_putRequestToUpdateTask_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        Task task = createTask(id);
        TaskResponse taskResponse = createTaskResponse(id);
        //when
        when(taskService.updateTask(anyLong(), any(TaskRequest.class))).thenReturn(task);
        when(taskMapper.apply(any(Task.class))).thenReturn(taskResponse);
        mockMvc.perform(put("/api/tasks/{id}/update_task", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"header\": \"Updated Task\", \"description\": \"Updated Description\", \"assignee\": \"assignee@example.com\", \"priority\": \"HIGH\", \"status\": \"IN_PROGRESS\"}"))
                .andExpect(status().isOk());
        verify(taskService, times(1)).updateTask(anyLong(), any(TaskRequest.class));

    }

    @Test
    void given_TaskId_when_DeleteRequestToDeleteTask_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        doNothing().when(taskService).deleteTask(id);
        // when then
        mockMvc.perform(delete("/api/tasks/{id}/delete_task", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Task with id " + id + " has been deleted"));
        verify(taskService, times(1)).deleteTask(anyLong());

    }

    @Test
    void given_TaskIdAndNewStatus_when_PatchRequestToChangeStatusInvoked_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        Status newStatus = Status.IN_PROGRESS;

        Task task = createTask(id).setStatus(newStatus);
        TaskResponse taskResponse = createTaskResponse(id).setStatus(newStatus);

        // when  then
        when(taskService.changeStatus(anyLong(), any(Status.class))).thenReturn(task);
        when(taskMapper.apply(any(Task.class))).thenReturn(taskResponse);

        mockMvc.perform(patch("/api/tasks/{id}/change_status", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .param("status", newStatus.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value(newStatus.name()));
        verify(taskService, times(1)).changeStatus(anyLong(), any(Status.class));
    }

    @Test
    void given_TaskIdAndAssignee_when_PatchRequestToSetAssignee_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        String newAssignee = "assignee1@example.com";

        Task task = createTask(id).setAssignee(newAssignee);
        TaskResponse taskResponse = createTaskResponse(id).setAssignee(newAssignee);

        // when  then
        when(taskService.setAssignee(anyLong(), any(String.class))).thenReturn(task);
        when(taskMapper.apply(any(Task.class))).thenReturn(taskResponse);

        mockMvc.perform(patch("/api/tasks/{id}/set_assignee", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .param("assignee", newAssignee)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.assignee").value(newAssignee));
        verify(taskService, times(1)).setAssignee(anyLong(), any(String.class));
    }

    @Test
    void given_TaskIdAndCommentRequest_when_PostRequestToAddComment_then_correctResponse() throws Exception {
        // given
        Long id = 1L;
        String commentDescription = "newComment";
        Task task = createTask(id);
        TaskResponse taskResponse = createTaskResponse(id);
        when(taskService.addComment(anyLong(), any(String.class))).thenReturn(task);
        when(taskMapper.apply(any(Task.class))).thenReturn(taskResponse);
        // when then
        mockMvc.perform(post("/api/tasks/{id}/add_comment", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.claim("test@example.com", "test@example.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"" + commentDescription + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        verify(taskService, times(1)).addComment(anyLong(), any(String.class));
    }
}