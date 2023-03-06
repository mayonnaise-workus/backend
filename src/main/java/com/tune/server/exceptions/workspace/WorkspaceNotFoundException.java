package com.tune.server.exceptions.workspace;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WorkspaceNotFoundException extends RuntimeException {
    private String message;

    public WorkspaceNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
