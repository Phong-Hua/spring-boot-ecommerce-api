package com.example.demo.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public abstract class APIRuntimeException extends RuntimeException {

    private @Getter @Setter HttpStatus status;

    public APIRuntimeException(HttpStatus status) {
        this.status = status;
    }

    public APIRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public APIRuntimeException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public APIRuntimeException(Throwable cause, HttpStatus status) {
        super(cause);
        this.status = status;
    }

    public APIRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, HttpStatus status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }
}
