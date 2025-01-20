package com.springboot;

import com.springboot.exceptions.JsonErrorResponseException;
import com.springboot.exceptions.UserUnauthenticatedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonErrorResponseException.class)
    public ResponseEntity<Map<String, String>> handleJsonErrorResponseException(JsonErrorResponseException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UserUnauthenticatedException.class)
    public String handleUserUnauthenticatedException(UserUnauthenticatedException ex) {
        return "redirect:/backend/auth/authenticate?next=" + ex.getRedirectUri();
    }
}