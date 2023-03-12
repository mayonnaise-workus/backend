package com.tune.server.repository;

import com.tune.server.domain.Tag;
import com.tune.server.domain.WorkspaceTag;
import com.tune.server.enums.TagTypeEnum;
import com.tune.server.enums.WorkspaceTagEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceTagRepository extends JpaRepository<WorkspaceTag, Long> {
    List<WorkspaceTag> findAllByTag_Id(Long tagId);

    List<WorkspaceTag> findAllByTypeAndTag_Id(WorkspaceTagEnum type, Long tagId);

    List<WorkspaceTag> findAllByWorkspace_Id(Long workspaceId);

}
