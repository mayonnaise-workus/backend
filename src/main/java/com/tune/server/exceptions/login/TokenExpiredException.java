package com.tune.server.exceptions.login;

import com.tune.server.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ErrorResponse {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
}
