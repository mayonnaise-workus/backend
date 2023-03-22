package com.tune.server.dto.request;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPreferenceRegionRequest {
    private List<Long> location_ids;

    public static MemberPreferenceRegionRequest of(Set<Integer> location_ids) {
        return MemberPreferenceRegionRequest.builder()
                .location_ids(location_ids.stream().map(Long::valueOf).collect(Collectors.toList()))
                .build();
    }
}
