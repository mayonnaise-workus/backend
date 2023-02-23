package com.tune.server.service.workspace;

import com.tune.server.dto.response.WorkspaceListResponse;

import java.util.List;

public interface WorkspaceService {
    List<WorkspaceListResponse> getWorkSpaceList(List<Long> regions);
}
