package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtSignatureException extends RuntimeException {
    public JwtSignatureException(String message) {
        super(message);
    }

    public JwtSignatureException(String message, Throwable cause) {
        super(message, cause);
    }
}
