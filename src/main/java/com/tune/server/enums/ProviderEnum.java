package com.tune.server.enums;

import lombok.Getter;

@Getter
public enum ProviderEnum {
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    GOOGLE("GOOGLE"),
    APPLE("APPLE");

    private String provider;

    ProviderEnum(String provider) {
        this.provider = provider;
    }
}
