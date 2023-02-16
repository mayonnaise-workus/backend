package com.tune.server.controller;

import com.tune.server.dto.request.TokenRequest;
import com.tune.server.service.member.MemberService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
public class TokenController {

    private MemberService memberService;

    @PostMapping("/refresh")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
                    @ApiResponse(responseCode = "404", description = "토큰 갱신 실패, 잘못된 토큰"),
            }
    )
    public ResponseEntity<Map<String, String>> refresh(@RequestBody TokenRequest request) {
        return ResponseEntity.ok(memberService.refresh(request.getRefresh_token()));
    }
}
