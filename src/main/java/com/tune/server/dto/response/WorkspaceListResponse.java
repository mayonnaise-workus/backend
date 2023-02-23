package com.tune.server.dto.response;

import com.tune.server.domain.WorkspaceTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Builder
public class WorkspaceListResponse {

    @ApiModelProperty(value = "워크스페이스 ID")
    private Long id;

    @ApiModelProperty(value = "워크스페이스 이름")
    private String name;

    @ApiModelProperty(value = "워크스페이스 도로명 주소")
    private String address;

    @ApiModelProperty(value = "워크스페이스 위도")
    private Double latitude;

    @ApiModelProperty(value = "워크스페이스 경도")
    private Double longitude;

    @ApiModelProperty(value = "워크스페이스 대표 프로필 이미지")
    private String profile_img;

    @Setter
    @ApiModelProperty(value = "워크스페이스 카테고리 (카페|스터디 카페|스터디룸|...)")
    List<Long> workspace_type;

    @Setter
    @ApiModelProperty(value = "워크스페이스 업무 목적 (노트북|대면 회의|비대면 회의)")
    List<Long> workspace_obj;

    @Setter
    @ApiModelProperty(value = "워크스페이스 수용 가능 인원")
    List<Long> workspace_capacity;

    public static WorkspaceListResponse of(WorkspaceTag workspaceTag) {
        return WorkspaceListResponse.builder()
                .id(workspaceTag.getWorkspace().getId())
                .name(workspaceTag.getWorkspace().getName())
                .address(workspaceTag.getWorkspace().getAddress())
                .latitude(workspaceTag.getWorkspace().getLatitude())
                .longitude(workspaceTag.getWorkspace().getLongitude())
                .profile_img(workspaceTag.getWorkspace().getProfileImg())
                .build();
    }

    @Override
    public int hashCode() {
        return id.intValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WorkspaceListResponse) {
            WorkspaceListResponse workspaceListResponse = (WorkspaceListResponse) obj;
            return this.id.equals(workspaceListResponse.getId());
        }
        return false;
    }
}
