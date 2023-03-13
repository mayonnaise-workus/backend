package com.tune.server.controller;

import com.tune.server.dto.response.MagazineResponse;
import com.tune.server.service.magazine.MagazineService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(tags = {"매거진 API"})
public class MagazineController {

    private MagazineService magazineService;

    @GetMapping("/magazine")
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
    public ResponseEntity<MagazineResponse> getMagazine() {
        return ResponseEntity.ok(
            magazineService.getAllMagazine()
        );
    }
}
