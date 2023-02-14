package com.tune.server.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class KakaoUserInfo {

    @NotNull
    private Long id;

    @NotNull
    private int expires_in;

    @NotNull
    private int app_id;
}
