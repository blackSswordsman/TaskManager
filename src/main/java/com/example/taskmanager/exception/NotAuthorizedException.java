package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You do not have rights to the source")
public class NotAuthorizedException extends ApplicationException {
    public NotAuthorizedException() {
        super("You do not have rights to the source");
    }


}