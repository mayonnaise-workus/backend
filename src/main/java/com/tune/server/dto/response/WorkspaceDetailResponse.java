package com.tune.server.dto.response;

import com.tune.server.domain.Workspace;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class WorkspaceDetailResponse {

    @ApiModelProperty(value = "워크스페이스 ID")
    private Long id;

    @ApiModelProperty(value = "워크스페이스 이름")
    private String name;

    @ApiModelProperty(value = "워크스페이스 도로명 주소")
    private String address;

    @ApiModelProperty(value = "워크스페이스 연락처")
    private String contact;

    @ApiModelProperty(value = "워크스페이스 위도")
    private Double latitude;

    @ApiModelProperty(value = "워크스페이스 경도")
    private Double longitude;

    @Setter
    @ApiModelProperty(value = "워크스페이스 카테고리 (카페|스터디 카페|스터디룸|...)")
    Long workspace_type;

    @Setter
    @ApiModelProperty(value = "워크스페이스 업무 목적 (노트북|대면 회의|비대면 회의)")
    Long workspace_obj;

    @ApiModelProperty(value = "워크스페이스 계약 여부")
    private boolean sponsor;

    @ApiModelProperty(value = "워크스페이스 대표 프로필 이미지")
    private String profile_img;

    @Setter
    @ApiModelProperty(value = "워크스페이스 이미지 리스트")
    private List<String> workspace_images;

    @ApiModelProperty(value = "워크스페이스 홈페이지")
    private String workspace_homepage;

    @ApiModelProperty(value = "워크스페이스 정보 페이지")
    private String workspace_info;

    @ApiModelProperty(value = "카카오맵 딥링크")
    private String kakao_url;

    @ApiModelProperty(value = "네이버맵 딥링크")
    private String naver_url;

    public static WorkspaceDetailResponse of(Workspace workspace) {
        return WorkspaceDetailResponse.builder()
            .id(workspace.getId())
            .name(workspace.getName())
            .address(workspace.getAddress())
            .contact(workspace.getContact())
            .latitude(workspace.getLatitude())
            .longitude(workspace.getLongitude())
            .profile_img(workspace.getProfileImg())
            .workspace_homepage(workspace.getHomepage())
            .workspace_info(workspace.getInfoUrl())
            .kakao_url(workspace.getKakaoUrl())
            .naver_url(workspace.getNaverUrl())
            .sponsor(workspace.isSponsor())
            .build();
    }
}
