package com.tune.server.controller;

import com.tune.server.dto.request.ValidateMemberNameRequest;
import com.tune.server.dto.response.ApiStatusResponse;
import com.tune.server.service.member.MemberService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(tags = {"검증 API"})
public class ValidationController {

    private MemberService memberService;

    @PostMapping("/validation/name")
    public ResponseEntity<ApiStatusResponse> validateMemberName(@RequestBody ValidateMemberNameRequest request) {
        return ResponseEntity.ok(
            memberService.validateName(request)
        );
    }

}
