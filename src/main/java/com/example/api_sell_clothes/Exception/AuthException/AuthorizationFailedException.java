package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException(String message) {
        super(message);
    }

    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
