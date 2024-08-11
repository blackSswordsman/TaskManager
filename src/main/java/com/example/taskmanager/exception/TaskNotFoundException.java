package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Task with this id does not exist")
public class TaskNotFoundException extends ApplicationException {
    public TaskNotFoundException() {
        super("Task with this id does not exist");
    }
}
