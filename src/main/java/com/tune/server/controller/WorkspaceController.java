package com.tune.server.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@AllArgsConstructor
@Api(tags = {"워크 스페이스 조회 API"})
public class WorkspaceController {

//    @GetMapping("/workspace/list")
//    public ResponseEntity<WorkSpaceListResponse> getWorkSpaceList() {
//        return ResponseEntity.ok(WorkSpaceListResponse.of());
//    }
}
