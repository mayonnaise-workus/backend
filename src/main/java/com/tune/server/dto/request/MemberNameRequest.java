package com.tune.server.dto.request;

import lombok.*;

import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberNameRequest {
    private String name;

    public static MemberNameRequest of(String name) {
        return MemberNameRequest.builder()
                .name(name)
                .build();
    }
}
