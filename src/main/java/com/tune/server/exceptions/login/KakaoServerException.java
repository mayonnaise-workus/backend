package com.tune.server.exceptions.login;

import com.tune.server.exceptions.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KakaoServerException extends ErrorResponse {
    public KakaoServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
