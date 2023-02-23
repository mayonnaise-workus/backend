package com.tune.server.service.workspace;

import com.tune.server.domain.Tag;
import com.tune.server.domain.WorkspaceTag;
import com.tune.server.dto.response.WorkspaceListResponse;
import com.tune.server.enums.TagTypeEnum;
import com.tune.server.enums.WorkspaceTagEnum;
import com.tune.server.repository.TagRepository;
import com.tune.server.repository.WorkspaceTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private WorkspaceTagRepository workspaceTagRepository;
    private TagRepository tagRepository;
    @Override
    public List<WorkspaceListResponse> getWorkSpaceList(List<Long> regions) {
        Set<WorkspaceListResponse> workspaceList = new HashSet<>();

        List<Tag> regionTags = new LinkedList<>();
        if (regions.size() == 0 || regions.get(0) == 0) {
            regionTags = tagRepository.findAllByType(TagTypeEnum.REGION);
        } else {
            for(Long region: regions) {
                regionTags.add(
                        tagRepository.findTagByTypeAndTagId(TagTypeEnum.REGION, region).orElseThrow(() -> new IllegalArgumentException("지역 태그가 존재하지 않습니다."))
                );
            }
        }


        for (Tag regionTag : regionTags) {
            List<WorkspaceTag> workspaceTags = workspaceTagRepository.findAllByTypeAndTag_Id(WorkspaceTagEnum.REGION, regionTag.getId());
            if (workspaceTags.size() == 0)
                continue;

            // 지역에 해당하는 워크스페이스 리스트를 저장 - 태그 불러와야 함.
            for (WorkspaceTag workspaceTag : workspaceTags) {
                workspaceList.add(WorkspaceListResponse.of(workspaceTag));
            }
        }

        for(WorkspaceListResponse workspace : workspaceList) {
            List<WorkspaceTag> workspaceTags = workspaceTagRepository.findAllByWorkspace_Id(workspace.getId());
            workspace.setWorkspace_type(workspaceTags.stream().filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.CATEGORY).map(workspaceTag -> workspaceTag.getTag().getTagId()).collect(Collectors.toList()));
            workspace.setWorkspace_obj(workspaceTags.stream().filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.PURPOSE).map(workspaceTag -> workspaceTag.getTag().getTagId()).collect(Collectors.toList()));
            workspace.setWorkspace_capacity(workspaceTags.stream().filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.CAPACITY).map(workspaceTag -> workspaceTag.getTag().getTagId()).collect(Collectors.toList()));
        }

        return List.copyOf(workspaceList);
    }
}