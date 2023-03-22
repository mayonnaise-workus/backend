package com.tune.server.dto.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    private boolean marketing_agreement;

    private boolean personal_information_agreement;

    private Set<Integer> purpose_ids;

    private Set<Integer> location_ids;

    private Set<Integer> workspace_purpose_ids;
}
