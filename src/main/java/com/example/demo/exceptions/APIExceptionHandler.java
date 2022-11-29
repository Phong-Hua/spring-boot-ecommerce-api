package com.example.demo.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class APIExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<APIErrorResponse> handleException(APIRuntimeException ex){
		APIErrorResponse response = new APIErrorResponseImpl(ex.getMessage(), ex.getStatus().value(), System.currentTimeMillis());

		return new ResponseEntity<>(response, ex.getStatus());
	}
}
