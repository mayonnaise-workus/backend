package com.tune.server.exceptions.login;

import com.tune.server.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidTokenException extends ErrorResponse {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}
