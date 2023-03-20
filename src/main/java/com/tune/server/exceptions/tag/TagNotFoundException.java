package com.tune.server.exceptions.tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TagNotFoundException extends RuntimeException {
    private String message;

    public TagNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
