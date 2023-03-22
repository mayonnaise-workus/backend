package com.tune.server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class GoogleLoginRequest {
    private String id;
    private String serverAuthCode;
}
