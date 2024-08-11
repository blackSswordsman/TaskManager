package com.example.taskmanager.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ORM представление задачи
 */
@Setter
@Getter
@ToString
@Table(name = "task")
@Entity
@Accessors(chain = true)
@RequiredArgsConstructor
public class Task extends Identifiable {

    /**
     * Заголовок задачи
     */
    @Column(name = "header")
    private String header;

    /**
     * Описание задачи
     */
    @Column(name = "description")
    private String description;

    /**
     * Статус задачи
     */
    @Column(name = "status")
    private Status status;

    /**
     * Приоритет задачи
     */
    @Column(name = "priority")
    private Priority priority;

    /**
     * Автор задачи
     */
    @Column(name = "creator")
    private String creator;

    /**
     * Исполнитель задачи
     */
    @Column(name = "assignee")
    private String assignee;

    /**
     * Список комментариев к задаче
     */
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    /**
     * Дата и время создания задачи
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Task setId(Long id) {
        this.id = id;
        return this;
    }

}
