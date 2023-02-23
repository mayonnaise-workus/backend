package com.tune.server.repository;

import com.tune.server.domain.WorkspaceTag;
import com.tune.server.enums.WorkspaceTagEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceTagRepository extends JpaRepository<WorkspaceTag, Long> {
    List<WorkspaceTag> findAllByTypeAndTag_Id(WorkspaceTagEnum type, Long tag_id);

    List<WorkspaceTag> findAllByWorkspace_Id(Long workspaceId);

}
