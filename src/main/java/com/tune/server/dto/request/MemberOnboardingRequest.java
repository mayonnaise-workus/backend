package com.tune.server.dto.request;

import io.swagger.annotations.ApiModelProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberOnboardingRequest {

    @ApiModelProperty(value = "회원 이름", required = true)
    private String name;

    @ApiModelProperty(value = "회원 업무 목적 리스트", required = true)
    private Set<Integer> purpose_ids;

    @ApiModelProperty(value = "회원 선호 지역 리스트", required = true)
    private Set<Integer> location_ids;

    @ApiModelProperty(value = "워크스페이스 카테고리 리스트", required = true)
    private Set<Integer> workspace_purpose_ids;

    @ApiModelProperty(value = "마케팅 수신 동의 여부", required = true)
    private boolean marketing_agreement;

    @ApiModelProperty(value = "개인정보 수집 및 이용 동의 여부", required = true)
    private boolean personal_information_agreement;
}
