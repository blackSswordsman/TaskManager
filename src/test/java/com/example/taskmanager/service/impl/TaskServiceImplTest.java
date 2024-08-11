package com.example.taskmanager.service.impl;

import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.exception.NotAuthorizedException;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UserEmailException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.utils.UserUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.example.taskmanager.utils.TestHelper.createTask;
import static com.example.taskmanager.utils.TestHelper.createTaskRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(TaskServiceImpl.class)
@Transactional
class TaskServiceImplTest {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskServiceImpl sut;


    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void given_TaskAndIdExistsInDB_when_getTaskByIdInvoked_then_returnsTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            // when
            Task actualResult = sut.getTaskById(id);
            // then
            assertNotNull(actualResult);
            assertEquals(task.getId(), actualResult.getId());
        }
    }

    @Test
    void given_TaskAndIdDoesNotExistsInDB_when_getTaskByIdInvoked_then_throwsTaskNotFoundException() {
        // given
        Long id = 1L;
        // when then
        assertThrows(TaskNotFoundException.class, () -> sut.getTaskById(id));
    }


    @Test
    void given_ValidTaskRequest_when_createTaskInvoked_then_returnsTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.empty());
            TaskRequest taskRequest = createTaskRequest();
            // when
            when(UserUtils.getCurrentUserEmail()).thenReturn(Optional.of("test@example.com"));
            Task actualResult = sut.createTask(taskRequest);
            // then
            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertEquals("test@example.com", actualResult.getCreator());
        }
    }

    @Test
    void given_TaskRequestWithInvalidCreatorEmail_when_createTaskInvoked_then_throwsUserEmailException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.empty());
            TaskRequest taskRequest = createTaskRequest();
            // when then
            assertThrows(UserEmailException.class, () -> sut.createTask(taskRequest));
        }

    }

    @Test
    void given_ValidIdAndTaskRequest_when_updateTaskInvoked_then_returnsUpdatedTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            TaskRequest taskRequest = createTaskRequest();
            // when
            Task actualResult = sut.updateTask(id, taskRequest);
            // then
            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertEquals(task.getId(), actualResult.getId());
        }
    }

    @Test
    void given_NotAuthorizedUser_when_updateTaskInvoked_then_throwsNotAuthorizedException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@exxample.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            TaskRequest taskRequest = createTaskRequest();
            // when then
            assertThrows(NotAuthorizedException.class, () -> sut.updateTask(id, taskRequest));
        }
    }

    @Test
    void given_TaskIdAndExistsInDB_when_deleteTaskInvoked_then_success() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            Optional<Task> foundTask = taskRepository.findById(id);
            assertTrue(foundTask.isPresent());
            // when
            sut.deleteTask(id);
            // then
            Optional<Task> deletedTask = taskRepository.findById(id);
            assertFalse(deletedTask.isPresent());
        }
    }

    @Test
    void given_TaskIdAndDoesNotExistInDB_when_deleteTaskInvoked_then_throwsTaskNotFoundException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Long id = 1L;
            // when then
            assertThrows(TaskNotFoundException.class, () -> sut.deleteTask(id));
        }
    }

    @Test
    void given_TaskIdWithUnAuthorizedCreatorEmail_when_deleteTaskInvoked_then_throwsNotAuthorizedException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("me@example.com"));
            Task task = createTask(null);
            taskRepository.save(task);
            Long id = task.getId();
            // when then
            assertThrows(NotAuthorizedException.class, () -> sut.deleteTask(id));
        }
    }

    @Test
    void given_TaskWithIdAndExistsInDB_when_changeStatusInvoked_then_returnsUpdatedTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            // when
            Task actualResult = sut.changeStatus(id, Status.COMPLETED);
            // then
            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertEquals(task.getId(), actualResult.getId());
            assertEquals(Status.COMPLETED, actualResult.getStatus());
        }
    }

    @Test
    void given_TaskWithIdAndDoesNotExistInDB_when_changeStatusInvoked_then_throwsTaskNotFoundException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            // when then
            assertThrows(TaskNotFoundException.class, () -> sut.changeStatus(1L, Status.COMPLETED));
        }
    }

    @Test
    void given_TaskIdWithUnAuthorizedCreatorEmail_when_changeStatusInvoked_then_throwsNotAuthorizedException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("me@example.com"));
            Task task = createTask(1L);
            taskRepository.save(task);
            Long id = task.getId();
            // when then
            assertThrows(NotAuthorizedException.class, () -> sut.changeStatus(id, Status.COMPLETED));
        }
    }

    @Test
    void given_TaskWithIdAndExistsInDB_when_setAssigneeInvoked_then_returnsUpdatedTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            // when
            Task actualResult = sut.setAssignee(id, "assignee@example.com");
            // then
            assertNotNull(actualResult);
            assertNotNull(actualResult.getId());
            assertEquals(task.getId(), actualResult.getId());
            assertEquals("assignee@example.com", actualResult.getAssignee());
        }
    }

    @Test
    void given_TaskWithIdAndDoesNotExistInDB_when_setAssigneeInvoked_then_throwsTaskNotFoundException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            // when then
            assertThrows(TaskNotFoundException.class, () -> sut.setAssignee(1L, "assignee@example.com"));
        }
    }

    @Test
    void given_TaskIdWithUnAuthorizedCreatorEmail_when_setAssigneeInvoked_then_throwsNotAuthorizedException() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("me@example.com"));
            Task task = createTask(null);
            taskRepository.save(task);
            Long id = task.getId();
            // when then
            assertThrows(NotAuthorizedException.class, () -> sut.setAssignee(id, "assignee@example.com"));
        }
    }

    @Test
    void given_ValidEmailAndPageable_when_getAllTasksByUserInvoked_then_returnsTaskPage() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            String email = "test@example.com";
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of(email));
            Pageable pageable = PageRequest.of(0, 10);
            Task task = createTask(null);
            task = taskRepository.save(task);
            // when
            Page<Task> actualPage = sut.getAllTasksByUser(email, pageable);
            // then
            assertNotNull(actualPage);
            assertEquals(1, actualPage.getTotalElements());
            assertEquals(task.getCreator(), actualPage.getContent().get(0).getCreator());
        }
    }

    @Test
    void given_TaskIdExistsInDBandValidCommentDescription_when_addCommentInvoke_then_returnsUpdatedTask() {
        // given
        try (MockedStatic<UserUtils> mockedStatic = Mockito.mockStatic(UserUtils.class)) {
            mockedStatic.when(UserUtils::getCurrentUserEmail).thenReturn(Optional.of("test@example.com"));
            Task task = createTask(null);
            task = taskRepository.save(task);
            Long id = task.getId();
            // when
            Task actualResult = sut.addComment(id, "comment");
            // then
            assertNotNull(task);
            assertNotNull(actualResult.getComments());
            assertEquals(task.getId(), actualResult.getId());
            assertEquals(actualResult.getComments().size(), 1);
            assertEquals(actualResult.getComments().get(0).getDescription(), "comment");
        }

    }
}