package com.tune.server.dto.request;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppleLoginRequest {
    private String authorizationCode;
    private String user;
}
