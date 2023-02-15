package com.tune.server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse extends RuntimeException {
    private String message;
    private int status;

    public ErrorResponse(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status.value();
    }

}
