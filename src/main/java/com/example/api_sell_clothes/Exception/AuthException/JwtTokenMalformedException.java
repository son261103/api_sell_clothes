package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtTokenMalformedException extends RuntimeException {
    public JwtTokenMalformedException(String message) {
        super(message);
    }

    public JwtTokenMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
