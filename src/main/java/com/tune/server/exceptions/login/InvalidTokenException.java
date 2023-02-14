package com.tune.server.exceptions.login;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    private String message;

    public InvalidTokenException(String message) {
        super(message);
        this.message = message;
    }
}
