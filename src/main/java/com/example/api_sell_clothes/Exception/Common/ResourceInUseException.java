package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class ResourceInUseException extends BaseException {
    public ResourceInUseException(String message) {
        super(message, HttpStatus.CONFLICT, "ERROR.RESOURCE_IN_USE");
    }

    public ResourceInUseException(String resourceName, String reason) {
        super(String.format("Cannot perform operation on %s because %s", resourceName, reason),
                HttpStatus.CONFLICT,
                "ERROR.RESOURCE_IN_USE");
    }
}