package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtUnsupportedException extends RuntimeException {
    public JwtUnsupportedException(String message) {
        super(message);
    }

    public JwtUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
