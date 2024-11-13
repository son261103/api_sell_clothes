package com.example.api_sell_clothes.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private String field;
    private Object rejectedValue;
    private String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

    public ValidationException(String field, Object rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    @Override
    public String getMessage() {
        return message;
    }
}