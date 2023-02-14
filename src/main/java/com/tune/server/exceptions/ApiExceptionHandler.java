package com.tune.server.exceptions;

import com.tune.server.exceptions.login.InvalidTokenException;
import com.tune.server.exceptions.login.KakaoServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(value = InvalidTokenException.class)
        public ResponseEntity<ApiErrorResponse> handleException(InvalidTokenException e) {
            ApiErrorResponse response = new ApiErrorResponse("ERROR-0001", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(value = KakaoServerException.class)
        public ResponseEntity<ApiErrorResponse> handleException(KakaoServerException e) {
            ApiErrorResponse response = new ApiErrorResponse("ERROR-0002", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
