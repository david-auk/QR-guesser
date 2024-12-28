package com.springboot;

import com.springboot.exceptions.QrGuesserUserShouldLoginException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(QrGuesserUserShouldLoginException.class)
    public String handleQrGuesserUserShouldLoginException(QrGuesserUserShouldLoginException ex) {
        return "redirect:/backend/auth/authenticate"; // TODO add mapping where was thrown to state
    }
}