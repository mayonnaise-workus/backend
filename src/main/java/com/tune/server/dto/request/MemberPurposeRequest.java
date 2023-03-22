package com.tune.server.dto.request;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPurposeRequest {
    private List<Long> purpose_ids;

    public static MemberPurposeRequest of(Set<Integer> purpose_ids) {
        return MemberPurposeRequest.builder()
                .purpose_ids(purpose_ids.stream().map(Long::valueOf).collect(Collectors.toList()))
                .build();
    }
}
