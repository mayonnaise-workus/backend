package com.tune.server.dto.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class KakaoTokenRequest {

    @NotNull
    @ApiModelProperty(value = "카카오 refresh token", required = true)
    private String refresh_token;

    @NotNull
    @ApiModelProperty(value = "카카오 access token", required = true)
    private String access_token;

    @NotNull
    @ApiModelProperty(value = "카카오 access token 만료 시간", required = true)
    private int expires_in;
}
