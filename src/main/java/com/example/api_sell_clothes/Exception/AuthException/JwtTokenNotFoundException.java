package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtTokenNotFoundException extends RuntimeException {
    public JwtTokenNotFoundException(String message) {
        super(message);
    }

    public JwtTokenNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
