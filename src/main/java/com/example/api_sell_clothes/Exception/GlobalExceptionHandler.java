package com.example.api_sell_clothes.Exception;

import com.example.api_sell_clothes.Exception.Common.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private LocalDateTime timestamp;
        private String message;
        private String errorCode;
        private String path;
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                ex.getErrorCode(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    // Handle specific exceptions if needed
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                "An unexpected error occurred",
                "ERROR.INTERNAL_SERVER",
                request.getDescription(false)
        );
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}