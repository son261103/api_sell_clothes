package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ERROR.FORBIDDEN");
    }
}