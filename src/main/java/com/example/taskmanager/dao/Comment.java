package com.example.taskmanager.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * ORM представление комментария к задаче
 */
@Setter
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name = "comment")
@Entity
public class Comment extends Identifiable {

    /**
     * Автор комментария
     */
    @Column(name = "author")
    private String author;

    /**
     * Задача к которой относится комментарий
     */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * Текст комментария
     */
    @Column(name = "description")
    private String description;

    /**
     * Дата и время создания комментария
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Comment setId(Long id) {
        this.id = id;
        return this;
    }


}
