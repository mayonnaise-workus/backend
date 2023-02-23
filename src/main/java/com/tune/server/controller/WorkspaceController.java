package com.tune.server.controller;

import com.tune.server.dto.response.WorkspaceListResponse;
import com.tune.server.service.workspace.WorkspaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(tags = {"워크 스페이스 조회 API"})
public class WorkspaceController {

    private WorkspaceService workspaceService;

    @GetMapping("/workspace/list")
    @ApiOperation(value = "워크 스페이스 리스트 조회", notes = "워크 스페이스 리스트를 조회합니다.")
    public ResponseEntity<List<WorkspaceListResponse>> getWorkSpaceList(@ApiIgnore Authentication authentication, @RequestParam(value = "region", required = false) List<Long> regions) {
        return ResponseEntity.ok(workspaceService.getWorkSpaceList(regions));
    }
}
