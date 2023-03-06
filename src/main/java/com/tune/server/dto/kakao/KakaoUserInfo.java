package com.tune.server.dto.kakao;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfo {

    @NotNull
    private Long id;

    @NotNull
    private int expires_in;

    @NotNull
    private int app_id;

    @Setter
    private String refreshToken;
}
