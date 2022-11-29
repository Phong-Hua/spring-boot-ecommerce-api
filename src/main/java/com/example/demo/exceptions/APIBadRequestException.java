package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;

public class APIBadRequestException extends APIRuntimeException {

    public APIBadRequestException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public APIBadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public APIBadRequestException(String message, Throwable cause) {
        super(message, cause, HttpStatus.BAD_REQUEST);
    }

    public APIBadRequestException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }

    public APIBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.BAD_REQUEST);
    }
}
