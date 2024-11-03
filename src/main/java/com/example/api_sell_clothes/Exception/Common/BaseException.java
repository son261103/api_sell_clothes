package com.example.api_sell_clothes.Exception.Common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final String errorCode;

    public BaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.message = message;
        this.status = status;
        this.errorCode = errorCode;
    }
}