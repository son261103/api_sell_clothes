package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "ERROR.UNAUTHORIZED");
    }
}