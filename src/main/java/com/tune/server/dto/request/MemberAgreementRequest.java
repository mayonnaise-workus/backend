package com.tune.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class MemberAgreementRequest {

    @NotNull(message = "개인정보 수집 및 이용에 대한 동의는 필수입니다.")
    private Boolean marketing_agreement;

    @NotNull(message = "개인정보 수집 및 이용에 대한 동의는 필수입니다.")
    private Boolean personal_information_agreement;

}
