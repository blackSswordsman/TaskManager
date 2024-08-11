package com.example.taskmanager.controller.dto;

import com.example.taskmanager.exception.ApplicationException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик exceptions приложения.
 * Если возникают иключения типа MethodArgumentNotValidException,ApplicationException,
 * он попытается вернуть форматированный вывод в response
 */
@SuppressWarnings(value = "unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Если в процессе валидации запроса пользователя или в процессе валидации аргументов какого-либо метода,
     * произойдет ошибка, которая выбрасывает MethodArgumentNotValidException,
     * вернет в респонсе форматированный вывод об этой ошибке.
     *
     * @return список филдов, которые не прошли валидацию, и ошибки валидации
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Если в процессе работы программы произойдет исключение приложения, которое мы специально выбрасываем
     * в некоторых случаях, вернется форматированный вывод (иначе код ошибки без данного метода) в респонсе, и
     * установит у респонса статус ответа из аннотации @ResponseStatus.
     */
    @ExceptionHandler(ApplicationException.class)
    protected ResponseEntity<Object> handleConflict(ApplicationException ex, WebRequest request) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            HttpStatus status = responseStatus.value();
            String reason = responseStatus.reason();
            if (reason.isEmpty()) {
                reason = ex.getMessage();
            }
            return ResponseEntity.status(status).body(new ErrorResponse(reason));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An unexpected error occurred"));
    }

    public record ErrorResponse(String message) {
    }
}