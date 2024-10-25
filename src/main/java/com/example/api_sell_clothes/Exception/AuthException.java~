package com.example.api_sell_clothes.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AuthException {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtExpiredException extends RuntimeException {
        public JwtExpiredException(String message) {
            super(message);
        }

        public JwtExpiredException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtInvalidException extends RuntimeException {
        public JwtInvalidException(String message) {
            super(message);
        }

        public JwtInvalidException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtSignatureException extends RuntimeException {
        public JwtSignatureException(String message) {
            super(message);
        }

        public JwtSignatureException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtMalformedException extends RuntimeException {
        public JwtMalformedException(String message) {
            super(message);
        }

        public JwtMalformedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtUnsupportedException extends RuntimeException {
        public JwtUnsupportedException(String message) {
            super(message);
        }

        public JwtUnsupportedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class JwtEmptyClaimException extends RuntimeException {
        public JwtEmptyClaimException(String message) {
            super(message);
        }

        public JwtEmptyClaimException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class AuthenticationFailedException extends RuntimeException {
        public AuthenticationFailedException(String message) {
            super(message);
        }

        public AuthenticationFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class AuthorizationFailedException extends RuntimeException {
        public AuthorizationFailedException(String message) {
            super(message);
        }

        public AuthorizationFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
