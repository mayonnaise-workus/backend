package com.tune.server.controller;

import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.dto.request.MemberNameRequest;
import com.tune.server.dto.response.MemberResponse;
import com.tune.server.service.member.MemberService;
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
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member")
    public ResponseEntity<MemberResponse> getMember(@ApiIgnore Authentication authentication) {
        return ResponseEntity.ok(MemberResponse.of(memberService.getInfo((MemberAuthDto) authentication.getPrincipal())));
    }

    @PostMapping("/member/agreement")
    public ResponseEntity<MemberResponse> updateAgreement(@ApiIgnore Authentication authentication, @RequestBody MemberAgreementRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updateAgreement((MemberAuthDto) authentication.getPrincipal(), request)));
    }

    @PostMapping("/member/name")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 - 이름 길이 초과 | 중복된 이름 | 필수 매개변수 없음")
    public ResponseEntity<MemberResponse> updateName(@ApiIgnore Authentication authentication, @RequestBody MemberNameRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.updateName((MemberAuthDto) authentication.getPrincipal(), request)));
    }
}
