package com.example.demo.exceptions;

public interface APIErrorResponse {

    long getTimestamp();

    String getMessage();

    int getStatus();
}
