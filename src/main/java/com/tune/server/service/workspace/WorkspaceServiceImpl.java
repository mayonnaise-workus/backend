package com.tune.server.service.workspace;

import com.tune.server.domain.Tag;
import com.tune.server.domain.Workspace;
import com.tune.server.domain.WorkspaceImage;
import com.tune.server.domain.WorkspaceTag;
import com.tune.server.dto.response.WorkspaceDetailResponse;
import com.tune.server.dto.response.WorkspaceListResponse;
import com.tune.server.enums.TagTypeEnum;
import com.tune.server.enums.WorkspaceTagEnum;
import com.tune.server.exceptions.workspace.WorkspaceNotFoundException;
import com.tune.server.repository.TagRepository;
import com.tune.server.repository.WorkspaceImageRepository;
import com.tune.server.repository.WorkspaceRepository;
import com.tune.server.repository.WorkspaceTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {
    private WorkspaceTagRepository workspaceTagRepository;
    private TagRepository tagRepository;

    private WorkspaceImageRepository workspaceImageRepository;
    private WorkspaceRepository workspaceRepository;
    @Override
    public List<WorkspaceListResponse> getWorkSpaceList(List<Long> regions) {
        Set<WorkspaceListResponse> workspaceList = new HashSet<>();

        // 지역 태그 전체를 가져와서 태그에 해당하는 워크스페이스 리스트를 저장
        List<WorkspaceTag> workspaceTags;

        if (regions == null || regions.size() == 0 || regions.get(0) == 0) {
            workspaceTags = workspaceTagRepository.findAll();
            // Workspace 기준으로 Set 구성
            for (WorkspaceTag workspaceTag : workspaceTags) {
                workspaceList.add(WorkspaceListResponse.of(workspaceTag));
            }
        } else {
            workspaceTags = new LinkedList<>();
            for (Long region : regions) {
                Tag tag  = tagRepository.findTagByTypeAndTagId(TagTypeEnum.REGION, region).orElseThrow(() -> new WorkspaceNotFoundException("태그가 존재하지 않습니다."));
                List<WorkspaceTag> tags = workspaceTagRepository.findAllByTypeAndTag_Id(WorkspaceTagEnum.REGION, tag.getId());

                for(WorkspaceTag workspaceTag: tags) {
                    workspaceTags.addAll(workspaceTagRepository.findAllByWorkspace_Id(workspaceTag.getWorkspace().getId()));
                    workspaceList.add(WorkspaceListResponse.of(workspaceTag));
                }

            }
        }

        // Workspace에 태그 정보 추가
        for(WorkspaceListResponse workspace : workspaceList) {
            List<WorkspaceTag> tags = workspaceTags.stream().filter(workspaceTag -> Objects.equals(workspaceTag.getWorkspace().getId(), workspace.getId())).collect(Collectors.toList());

            // tags를 WorkspaceTagEnum 에 따라 Grouping
            Map<WorkspaceTagEnum, List<WorkspaceTag>> tagMap = tags.stream().collect(Collectors.groupingBy(WorkspaceTag::getType));

            // WorkspaceTagEnum에 따라 WorkspaceListResponse에 태그 정보 추가
            for (WorkspaceTagEnum tagEnum : tagMap.keySet()) {
                switch (tagEnum) {
                    case CATEGORY:
                        workspace.setWorkspace_type(tagMap.get(tagEnum).get(0).getTag().getTagId());
                        break;
                    case PURPOSE:
                        workspace.setWorkspace_obj(tagMap.get(tagEnum).get(0).getTag().getTagId());
                        break;
                    case CAPACITY:
                        workspace.setWorkspace_capacity(tagMap.get(tagEnum).get(0).getTag().getTagId());
                        break;
                    case REGION:
                        workspace.setWorkspace_region(tagMap.get(tagEnum).get(0).getTag().getTagId());
                        break;
                }
            }
        }

        return List.copyOf(workspaceList);
    }

    @Override
    public WorkspaceDetailResponse getWorkSpaceDetail(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(() -> new WorkspaceNotFoundException("워크스페이스가 존재하지 않습니다."));

        WorkspaceDetailResponse workspaceDetailResponse = WorkspaceDetailResponse.of(workspace);

        List<WorkspaceTag> workspaceTags = workspaceTagRepository.findAllByWorkspace_Id(workspace.getId());
        workspaceDetailResponse.setWorkspace_type(workspaceTags.stream().filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.CATEGORY).map(workspaceTag -> workspaceTag.getTag().getTagId()).collect(Collectors.toList()).get(0));
        workspaceDetailResponse.setWorkspace_obj(workspaceTags.stream().filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.PURPOSE).map(workspaceTag -> workspaceTag.getTag().getTagId()).collect(Collectors.toList()).get(0));

        List<WorkspaceImage> workspaceImages = workspaceImageRepository.findWorkspaceImagesByWorkspace_Id(workspace.getId());
        workspaceDetailResponse.setWorkspace_images(workspaceImages.stream().map(WorkspaceImage::getImageUrl).collect(Collectors.toList()));

        return workspaceDetailResponse;
    }

    @Override
    public Workspace findWorkSpaceById(Long workspaceId) {
        return workspaceRepository.findById(workspaceId).orElseThrow(() -> new WorkspaceNotFoundException("워크스페이스가 존재하지 않습니다."));
    }
}
