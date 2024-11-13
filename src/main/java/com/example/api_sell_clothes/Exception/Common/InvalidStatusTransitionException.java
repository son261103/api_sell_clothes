package com.example.api_sell_clothes.Exception.Common;

public class InvalidStatusTransitionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidStatusTransitionException(String message) {
        super(message);
    }

    public InvalidStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStatusTransitionException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("Invalid status transition for %s with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}