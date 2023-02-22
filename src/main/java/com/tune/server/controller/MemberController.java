package com.tune.server.controller;

import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.dto.request.MemberNameRequest;
import com.tune.server.dto.request.MemberPreferenceRegionRequest;
import com.tune.server.dto.request.MemberPurposeRequest;
import com.tune.server.dto.response.MemberOnboardingResponse;
import com.tune.server.dto.response.MemberPreferenceResponse;
import com.tune.server.dto.response.MemberResponse;
import com.tune.server.service.member.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@AllArgsConstructor
@Api(tags = {"Member 조회 API"})
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원 정보 조회", notes = "회원 정보를 조회합니다.")
    public ResponseEntity<MemberResponse> getMember(@ApiIgnore Authentication authentication) {
        return ResponseEntity.ok(MemberResponse.of(memberService.getInfo((MemberAuthDto) authentication.getPrincipal())));
    }

    @GetMapping("/member/onboarding")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원 온보딩 상태 조회", notes = "회원 온보딩 상태를 조회합니다.")
    public ResponseEntity<MemberOnboardingResponse> getMemberOnboarding(@ApiIgnore Authentication authentication) {
        return ResponseEntity.ok(memberService.getOnboardingStatus((MemberAuthDto) authentication.getPrincipal()));
    }

    @PostMapping("/member/agreement")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원 약관 동의", notes = "회원 약관 동의를 합니다.")
    public ResponseEntity<MemberResponse> updateAgreement(@ApiIgnore Authentication authentication, @RequestBody MemberAgreementRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updateAgreement((MemberAuthDto) authentication.getPrincipal(), request)));
    }

    @PostMapping("/member/name")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원 이름 변경", notes = "회원 이름을 변경합니다.")
    public ResponseEntity<MemberResponse> updateName(@ApiIgnore Authentication authentication, @RequestBody MemberNameRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updateName((MemberAuthDto) authentication.getPrincipal(), request)));
    }

    @PostMapping("/member/preference/region")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원 지역 선호도 변경", notes = "회원 지역 선호도를 변경합니다.")
    public ResponseEntity<MemberResponse> updatePreferenceLocation(@ApiIgnore Authentication authentication, @RequestBody MemberPreferenceRegionRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updatePreferenceLocation((MemberAuthDto) authentication.getPrincipal(), request)));
    }

    @PostMapping("/member/preference/purpose")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원의 업무 목적 변경", notes = "회원의 업무 목적을 변경합니다.")
    public ResponseEntity<MemberResponse> updatePreferencePurpose(@ApiIgnore Authentication authentication, @RequestBody MemberPurposeRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updatePurpose((MemberAuthDto) authentication.getPrincipal(), request)));
    }

    @PostMapping("/member/preference/workspace-purpose")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 필수 매개변수 누락"),
                    @ApiResponse(responseCode = "401", description = "인증 실패 - 토큰 만료, 토큰 무효"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 혹은 잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @ApiOperation(value = "회원의 워크스페이스 업무 목적 변경", notes = "회원의 워크스페이스 업무 목적을 변경합니다.")
    public ResponseEntity<MemberResponse> updatePreferenceWorkspacePurpose(@ApiIgnore Authentication authentication, @RequestBody MemberPurposeRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updateWorkspacePurpose((MemberAuthDto) authentication.getPrincipal(), request)));
    }

}
