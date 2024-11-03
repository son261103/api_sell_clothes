package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Resource not found";

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "ERROR.NOT_FOUND");
    }

    public NotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.NOT_FOUND,
                "ERROR.NOT_FOUND");
    }
}