package com.tune.server.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MemberPreferenceResponse {

    private List<Long> preference_ids;
}
