package com.tune.server.dto.response;

import com.tune.server.domain.MemberScrap;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberScrapListResponse {
    List<WorkspaceListResponse> list;

    public static MemberScrapListResponse of(List<MemberScrap> memberScraps) {
        return MemberScrapListResponse.builder()
                .list(WorkspaceListResponse.of(memberScraps))
                .build();
    }
}
