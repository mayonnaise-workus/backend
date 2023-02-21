package com.tune.server.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberOnboardingResponse {

    @ApiModelProperty(value = "이용 약관 온보딩 진행 여부", example = "true|false")
    private Boolean terms_of_service_status;

    @ApiModelProperty(value = "닉네임 설정 온보딩 진행 여부", example = "true|false")
    private Boolean nickname_status;

    @ApiModelProperty(value = "선호 지역 설정 온보딩 진행 여부", example = "true|false")
    private Boolean member_preference_region_status;

    @ApiModelProperty(value = "업무 목적 설정 온보딩 진행 여부", example = "true|false")
    private Boolean member_purpose_status;

    @ApiModelProperty(value = "선호 워크스페이스 설정 온보딩 진행 여부", example = "true|false")
    private Boolean member_preference_workspace_status;

}
