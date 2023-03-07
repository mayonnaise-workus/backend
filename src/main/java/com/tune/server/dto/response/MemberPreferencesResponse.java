package com.tune.server.dto.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MemberPreferencesResponse {

    @ApiModelProperty(value = "회원 선호 워크스페이스 선호 지역", example = "[1, 2, 3]")
    List<Integer> preference_workspace_regions;

    @ApiModelProperty(value = "회원 선호 워크스페이스 카테고리", example = "[1, 2, 3]")
    List<Integer> preference_workspace_types;

    @ApiModelProperty(value = "회원 선호 워크스페이스 사용 목적", example = "[1, 2, 3]")
    List<Integer> preference_workspace_purposes;
}
