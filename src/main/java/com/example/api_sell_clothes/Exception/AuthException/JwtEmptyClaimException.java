package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtEmptyClaimException extends RuntimeException {
    public JwtEmptyClaimException(String message) {
        super(message);
    }

    public JwtEmptyClaimException(String message, Throwable cause) {
        super(message, cause);
    }
}
