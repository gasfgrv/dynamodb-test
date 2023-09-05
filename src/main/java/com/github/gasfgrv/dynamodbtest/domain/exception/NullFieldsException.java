package com.github.gasfgrv.dynamodbtest.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NullFieldsException extends RuntimeException {
    public NullFieldsException(String message) {
        super(message);
    }

}
