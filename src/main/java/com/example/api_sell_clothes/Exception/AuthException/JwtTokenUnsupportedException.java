package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtTokenUnsupportedException extends RuntimeException {
    public JwtTokenUnsupportedException(String message) {
        super(message);
    }

    public JwtTokenUnsupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
