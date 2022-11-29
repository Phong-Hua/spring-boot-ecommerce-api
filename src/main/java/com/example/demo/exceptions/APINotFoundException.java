package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;

public class APINotFoundException extends APIRuntimeException {

    public APINotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public APINotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public APINotFoundException(String message, Throwable cause) {
        super(message, cause, HttpStatus.NOT_FOUND);
    }

    public APINotFoundException(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }

    public APINotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace, HttpStatus.NOT_FOUND);
    }
}
