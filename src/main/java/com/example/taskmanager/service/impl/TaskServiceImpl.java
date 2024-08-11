package com.example.taskmanager.service.impl;

import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.dao.Comment;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;
import com.example.taskmanager.exception.NotAuthorizedException;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.exception.UserEmailException;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.service.api.TaskService;
import com.example.taskmanager.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the TaskService interface.
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    /**
     * Метод возвращает email авторизированного пользователя или выбрасывает исключение UserEmailException
     *
     * @return email пользователя || соответствующее исключение
     */
    private static String getUserEmailOrElseThrow() {
        return UserUtils.getCurrentUserEmail().orElseThrow(UserEmailException::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task createTask(TaskRequest taskRequest) {
        Task task = new Task().setCreator(getUserEmailOrElseThrow()).setAssignee(taskRequest.getAssignee()).setHeader(taskRequest.getHeader()).setDescription(taskRequest.getDescription()).setPriority(taskRequest.getPriority()).setStatus(taskRequest.getStatus()).setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task updateTask(Long id, TaskRequest taskRequest) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        String userEmail = getUserEmailOrElseThrow();
        if (!userEmail.equals(task.getCreator())) {
            throw new NotAuthorizedException();
        }
        task.setDescription(taskRequest.getDescription()).setPriority(taskRequest.getPriority()).setAssignee(taskRequest.getAssignee()).setHeader(taskRequest.getHeader()).setStatus(taskRequest.getStatus());
        return taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        String userEmail = getUserEmailOrElseThrow();
        if (!userEmail.equals(task.getCreator())) {
            throw new NotAuthorizedException();
        }
        taskRepository.delete(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task changeStatus(Long id, Status status) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        String userEmail = getUserEmailOrElseThrow();
        if (!userEmail.equals(task.getCreator()) && !userEmail.equals(task.getAssignee())) {
            throw new NotAuthorizedException();
        }
        task.setStatus(status);
        return taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task setAssignee(Long id, String assignee) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        String userEmail = getUserEmailOrElseThrow();
        if (!userEmail.equals(task.getCreator())) {
            throw new NotAuthorizedException();
        }
        task.setAssignee(assignee);
        return taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Task> getAllTasksByUser(String email, Pageable pageable) {
        return taskRepository.findTasksByUsersEmail(email, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task addComment(Long id, String commentDescription) {
        Task task = taskRepository.findById(id).orElseThrow(TaskNotFoundException::new);
        Comment comment1 = new Comment().setAuthor(task.getCreator()).setDescription(commentDescription).setCreatedAt(LocalDateTime.now()).setTask(task);
        task.getComments().add(comment1);
        return taskRepository.save(task);
    }


}
