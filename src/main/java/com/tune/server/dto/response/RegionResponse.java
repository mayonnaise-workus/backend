package com.tune.server.dto.response;


import com.tune.server.domain.MemberPreferenceRegion;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class RegionResponse {
    private Long id;
    private String name;

    public static RegionResponse of(MemberPreferenceRegion memberPreferenceRegion) {
        return RegionResponse.builder()
                .id(memberPreferenceRegion.getRegion().getId())
                .name(memberPreferenceRegion.getRegion().getName())
                .build();
    }

    public static List<RegionResponse> of(List<MemberPreferenceRegion> memberPreferenceRegion) {
        return memberPreferenceRegion.stream()
                .map(RegionResponse::of)
                .collect(Collectors.toList());
    }
}
