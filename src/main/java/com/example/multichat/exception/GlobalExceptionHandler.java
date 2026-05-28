package com.example.multichat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AllModelsFailedException.class)
    public ResponseEntity<Map<String, Object>> handleAllModelsFailed(AllModelsFailedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "All fallback models failed");
        body.put("message", ex.getMessage());
        body.put("errorsLog", ex.getErrorsLog());
        body.put("totalDurationMs", ex.getTotalDuration());
        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}