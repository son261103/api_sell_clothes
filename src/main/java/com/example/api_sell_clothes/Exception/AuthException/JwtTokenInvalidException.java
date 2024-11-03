package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtTokenInvalidException extends RuntimeException {
    public JwtTokenInvalidException(String message) {
        super(message);
    }

    public JwtTokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
