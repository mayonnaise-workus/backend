package com.tune.server.dto.response;


import com.tune.server.domain.MemberPreference;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@Builder
public class RegionResponse {
    private Long id;
    private String name;

    public static RegionResponse of(MemberPreference memberPreferenceRegion) {
        return RegionResponse.builder()
                .id(memberPreferenceRegion.getTag().getId())
                .name(memberPreferenceRegion.getTag().getTagName())
                .build();
    }

    public static List<RegionResponse> of(Set<MemberPreference> memberPreferenceRegion) {
        return memberPreferenceRegion.stream()
                .map(RegionResponse::of)
                .collect(Collectors.toList());
    }
}
