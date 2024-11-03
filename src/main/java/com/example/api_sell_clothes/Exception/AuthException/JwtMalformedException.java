package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtMalformedException extends RuntimeException {
    public JwtMalformedException(String message) {
        super(message);
    }

    public JwtMalformedException(String message, Throwable cause) {
        super(message, cause);
    }
}
