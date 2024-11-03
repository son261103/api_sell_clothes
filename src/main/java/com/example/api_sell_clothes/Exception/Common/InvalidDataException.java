package com.example.api_sell_clothes.Exception.Common;

import org.springframework.http.HttpStatus;

public class InvalidDataException extends BaseException {
    public InvalidDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "ERROR.INVALID_DATA");
    }
}