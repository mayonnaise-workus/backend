package com.tune.server.dto.response;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberPreferenceRegion;
import com.tune.server.domain.Region;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MemberResponse {

    @ApiModelProperty(value = "회원 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "회원 닉네임", example = "도도")
    private String name;

    @ApiModelProperty(value = "마케팅 수신 여부", example = "true|false|null")
    private Boolean marketing_agreement;

    @ApiModelProperty(value = "개인정보 수집 여부", example = "true|false|null")
    private Boolean personal_information_agreement;

    @ApiModelProperty(value = "회원 선호 지역", example = "1|2|3")
    private List<RegionResponse> member_preference_region;

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .marketing_agreement(member.getMarketingAgreement())
                .personal_information_agreement(member.getPersonalInformationAgreement())
                .member_preference_region(RegionResponse.of(member.getMemberPreferenceRegion()))
                .build();
    }
}
