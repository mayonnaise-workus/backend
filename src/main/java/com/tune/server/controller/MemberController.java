package com.tune.server.controller;

import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.dto.response.MemberResponse;
import com.tune.server.service.member.MemberService;
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
}
