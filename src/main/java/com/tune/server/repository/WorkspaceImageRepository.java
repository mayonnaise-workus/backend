package com.tune.server.repository;

import com.tune.server.domain.WorkspaceImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceImageRepository extends JpaRepository<WorkspaceImage, Long> {

    List<WorkspaceImage> findWorkspaceImagesByWorkspace_Id(Long workspace_id);
}
