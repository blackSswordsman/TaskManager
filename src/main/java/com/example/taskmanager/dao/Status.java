package com.example.taskmanager.dao;

/**
 * Перечисление статусов задачи
 */
public enum Status {
    /**
     * В ожидании
     */
    PENDING,
    /**
     * В процессе выполнения
     */
    IN_PROGRESS,
    /**
     * Завершена
     */
    COMPLETED
}
