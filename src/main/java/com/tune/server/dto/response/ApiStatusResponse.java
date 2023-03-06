package com.tune.server.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiStatusResponse {

    private int status;
    private String message;
}
