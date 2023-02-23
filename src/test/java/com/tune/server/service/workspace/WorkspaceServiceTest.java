package com.tune.server.service.workspace;

import com.tune.server.dto.response.WorkspaceListResponse;
import com.tune.server.service.member.MemberServiceImpl;
import com.tune.server.util.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import({MemberServiceImpl.class, WorkspaceServiceImpl.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkspaceServiceTest {

    @Autowired
    private WorkspaceService workspaceService;
    @BeforeAll
    void beforeAll() throws SQLException {
        JwtUtil.JWT_SECRET_KEY = "Jo73VnKMoZCBEgBloGffXFTDsZxRZ9fN5geXS3nX0wE";
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 지역에 맞는 워크스페이스를 조회한다.")
    void getWorkSpaceList() {
        // given
        List<Long> regions = List.of(1L);

        // when
        List<WorkspaceListResponse> workSpaceList = workspaceService.getWorkSpaceList(regions);

        // then
        assertEquals(1, workSpaceList.size());
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 지역이 여러개인 경우 여러 개의 워크스페이스를 조회한다.")
    void getWorkSpaceList2() {
        // given
        List<Long> regions = List.of(1L, 2L);

        // when
        List<WorkspaceListResponse> workSpaceList = workspaceService.getWorkSpaceList(regions);

        // then
        assertEquals(2, workSpaceList.size());
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 지역이 여러개인 경우 여러 개의 워크스페이스를 조회한다.")
    void getWorkSpaceList3() {
        // given
        List<Long> regions = List.of(1L, 2L, 3L, 4L, 5L);

        // when
        List<WorkspaceListResponse> workSpaceList = workspaceService.getWorkSpaceList(regions);

        // then
        assertEquals(5, workSpaceList.size());
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 지역이 범위에 없는 경우 400 에러를 반환한다.")
    void getWorkSpaceList4() {
        // given
        List<Long> regions = List.of(99L);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> workspaceService.getWorkSpaceList(regions));
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 선택한 지역이 없는 경우 전체 워크스페이스를 조회한다.")
    void getWorkSpaceList5() {
        // given
        List<Long> regions = List.of();

        // when
        List<WorkspaceListResponse> workSpaceList = workspaceService.getWorkSpaceList(regions);

        // then
        assertEquals(5, workSpaceList.size());
    }

    @Test
    @DisplayName("워크스페이스 리스트 조회 - 선택한 지역이 0번인 경우 전체 워크스페이스를 조회한다.")
    void getWorkSpaceList6() {
        // given
        List<Long> regions = List.of(0L);

        // when
        List<WorkspaceListResponse> workSpaceList = workspaceService.getWorkSpaceList(regions);

        // then
        assertEquals(5, workSpaceList.size());
    }
}
