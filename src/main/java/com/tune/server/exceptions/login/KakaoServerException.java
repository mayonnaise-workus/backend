package com.tune.server.exceptions.login;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KakaoServerException extends RuntimeException {
    private String message;

    public KakaoServerException(String message) {
        super(message);
        this.message = message;
    }
}
