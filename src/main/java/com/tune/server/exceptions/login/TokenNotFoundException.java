package com.tune.server.exceptions.login;

import com.tune.server.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends ErrorResponse {
    public TokenNotFoundException(String message) {
        super(message, HttpStatus.FORBIDDEN.value());
    }
}
