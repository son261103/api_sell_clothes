package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends BaseException {
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "ERROR.DUPLICATE_RESOURCE");
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue),
                HttpStatus.CONFLICT,
                "ERROR.DUPLICATE_RESOURCE");
    }
}