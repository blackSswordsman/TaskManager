package com.example.taskmanager.service.api;

import com.example.taskmanager.controller.dto.TaskRequest;
import com.example.taskmanager.dao.Status;
import com.example.taskmanager.dao.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Интерфейс сервисного слоя приложения
 */
public interface TaskService {

    /**
     * Метод находит задачу по ее id
     *
     * @param id -- идентификатор задачи
     * @return сама задача
     */
    Task getTaskById(Long id);

    /**
     * Метод создает новую задачу по переданному объекту TaskRequest
     *
     * @param taskRequest -- экземпляр dto задачи
     * @return созданная задача
     */
    Task createTask(TaskRequest taskRequest);

    /**
     * Метод редактирует уже существующую задачу по ее id и переданному объекту TaskRequest
     *
     * @param id          --идентификатор задачи
     * @param taskRequest - экземпляр dto задачи
     * @return обновленная задача
     */
    Task updateTask(Long id, TaskRequest taskRequest);

    /**
     * Метод удаляет задачу по ее id
     *
     * @param id --идентификатор задачи
     */
    void deleteTask(Long id);

    /**
     * Метод позволяет менять статус задачи по ее id и переданному новому статусу
     *
     * @param id     --идентификатор задачи
     * @param status --новый статус задачи
     * @return обновленная задача
     */
    Task changeStatus(Long id, Status status);

    /**
     * Метод позволяет назначть исполнителя задачи по ее id и преданному исполнителю в виде строки (email)
     *
     * @param id       --идентификатор задачи
     * @param assignee --email исполнителя задачи
     * @return обновленная задача
     */
    Task setAssignee(Long id, String assignee);

    /**
     * Метод возвращает страницу со всеми задачами пользователя
     *
     * @param email    --email пользователя, чьи задачи нужно отобразить
     * @param pageable --информация для пагинации
     * @return страница со всеми задачами пользователя
     */
    Page<Task> getAllTasksByUser(String email, Pageable pageable);

    /**
     * Метод добавляет комментарий к определенной задаче
     *
     * @param id      --идентификатор задачи
     * @param comment --body комментария
     * @return обновленная задача с добавленным комментарием
     */
    Task addComment(Long id, String comment);
}
