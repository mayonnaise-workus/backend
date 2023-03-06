package com.tune.server.service.workspace;

import com.tune.server.domain.Workspace;
import com.tune.server.dto.response.WorkspaceDetailResponse;
import com.tune.server.dto.response.WorkspaceListResponse;

import java.util.List;

public interface WorkspaceService {
    List<WorkspaceListResponse> getWorkSpaceList(List<Long> regions);

    WorkspaceDetailResponse getWorkSpaceDetail(Long workspaceId);

    Workspace findWorkSpaceById(Long workspaceId);
}
