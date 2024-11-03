package com.example.api_sell_clothes.Exception.AuthException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Đánh dấu ngoại lệ này với mã trạng thái HTTP 403 (Forbidden)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    // Constructor không tham số
    public ForbiddenException() {
        super("Access is forbidden"); // Tin nhắn mặc định
    }

    // Constructor với tin nhắn tùy chỉnh
    public ForbiddenException(String message) {
        super(message);
    }

    // Constructor với tin nhắn và nguyên nhân
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor với nguyên nhân
    public ForbiddenException(Throwable cause) {
        super(cause);
    }
}
