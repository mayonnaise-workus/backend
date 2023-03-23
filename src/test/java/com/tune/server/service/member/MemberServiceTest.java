package com.tune.server.service.member;

import com.tune.server.domain.*;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.dto.request.MemberNameRequest;
import com.tune.server.dto.request.MemberOnboardingRequest;
import com.tune.server.dto.request.MemberPreferenceRegionRequest;
import com.tune.server.dto.request.MemberPurposeRequest;
import com.tune.server.dto.response.ApiStatusResponse;
import com.tune.server.dto.response.MemberOnboardingResponse;
import com.tune.server.dto.response.MemberPreferencesResponse;
import com.tune.server.dto.response.MemberScrapListResponse;
import com.tune.server.enums.MemberPreferenceEnum;
import com.tune.server.exceptions.login.TokenExpiredException;
import com.tune.server.exceptions.member.InvalidRequestException;
import com.tune.server.exceptions.member.MemberNotFoundException;
import com.tune.server.exceptions.workspace.WorkspaceNotFoundException;
import com.tune.server.repository.*;
import com.tune.server.service.AuthService;
import com.tune.server.service.workspace.WorkspaceServiceImpl;
import com.tune.server.util.JwtUtil;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({MemberServiceImpl.class, WorkspaceServiceImpl.class, AuthService.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberServiceTest {

    @Autowired
    private MemberProviderRepository memberProviderRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPreferenceRepository memberPreferenceRepository;

    @Autowired
    private MemberService memberService;

    @BeforeAll
    void beforeAll() throws SQLException {
        JwtUtil.JWT_SECRET_KEY = "Jo73VnKMoZCBEgBloGffXFTDsZxRZ9fN5geXS3nX0wE";
    }
    @Test
    @DisplayName("회원가입 테스트 - 이미 존재하는 회원인 경우 회원가입을 진행하지 않고, 기존에 존재하는 정보를 되돌려준다.")
    void isExistMember_success() {
        // given
        Member member = memberRepository.save(Member.builder()
                .name("test")
                .build());

        MemberProvider memberProvider = MemberProvider.builder()
                .member(member)
                .providerId("99")
                .provider("KAKAO")
                .build();

        // when
        memberService.signUp(KakaoUserInfo.builder()
                .app_id(99)
                .expires_in(1)
                .build());


        // then
        assertEquals(2, memberRepository.count());
    }

    @Test
    @DisplayName("회원가입 테스트 - 카카오를 통하여 회원가입을 진행한다.")
    void isExistMember_fail() {
        // given
        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .id(1L)
                .expires_in(1)
                .refreshToken("test")
                .app_id(99)
                .build();

        // when
        boolean status = memberService.signUp(kakaoUserInfo);

        // then
        assertTrue(status);
        assertEquals(2, memberRepository.count());
        assertEquals(2, memberProviderRepository.count());
    }

    @Test
    @DisplayName("회원가입 테스트 - 10명의 유저가 카카오로 회원 가입을 시도한다.")
    void signUp_10() {
        // given
        List<KakaoUserInfo> kakaoUserInfoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            kakaoUserInfoList.add(KakaoUserInfo.builder()
                    .id((long) i)
                    .expires_in(1)
                    .app_id(1)
                    .build());
        }

        // when
        for (int i = 0; i < 10; i++) {
            boolean result = memberService.signUp(kakaoUserInfoList.get(i));
            assertTrue(result);
        }

        // then
        assertEquals(11, memberRepository.count());
    }

    @Test
    @DisplayName("회원 조회 테스트 - 카카오 유저의 정보를 조회한다.")
    void getMember() {
        // given
        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .id(1L)
                .expires_in(1)
                .app_id(1)
                .build();
        memberService.signUp(kakaoUserInfo);

        // when
        Member member = memberService.getMember(kakaoUserInfo);

        // then
        assertNotNull(member);
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하지 않으면 404 에러를 반환한다.")
    void refresh() {
        // given
        String refreshToken = "refresh_token";

        // when & when
        assertThrows(MemberNotFoundException.class, () -> memberService.refresh(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하고 만료까지 1달 이상 남았다면 access_token 토큰 갱신한다.")
    void refresh2() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(60);
        memberRepository.save(Member.builder()
                .refreshToken("refresh_token")
                .refreshTokenExpiresAt(expiresAt)
                .build());

        // when
        Map<String, String> result = memberService.refresh("refresh_token");

        // then
        assertNotNull(result.get("access_token"));
        assertNull(result.get("refresh_token"));
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하고 만료까지 1달 미만 남았다면 refresh_token과 access_token 토큰을 갱신한다.")
    void refresh3() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(15);
        memberRepository.save(Member.builder()
                .refreshToken("refresh_token")
                .refreshTokenExpiresAt(expiresAt)
                .build());

        // when
        Map<String, String> result = memberService.refresh("refresh_token");

        // then
        assertNotNull(result.get("access_token"));
        assertNotNull(result.get("refresh_token"));
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 만료되었다면 401 에러를 반환한다.")
    void refresh4() {
        // given
        memberRepository.save(Member.builder()
                .refreshToken("refresh_token")
                .refreshTokenExpiresAt(LocalDateTime.of(2021, 12, 31, 0, 0, 0))
                .build());

        // when & then
        assertThrows(TokenExpiredException.class, () -> memberService.refresh("refresh_token"));
    }

    @Test
    @DisplayName("약관 동의 테스트 - 어떠한 매개변수라도 null이면 400 에러 반환한다.")
    void agreeTerms() {
        // given
        Member member = Member.builder()
                .id(99L)
                .build();
        MemberAgreementRequest memberAgreementRequest = MemberAgreementRequest.builder()
                .build();
        MemberAgreementRequest memberAgreementRequest2 = MemberAgreementRequest.builder()
                .marketing_agreement(true)
                .build();
        MemberAgreementRequest memberAgreementRequest3 = MemberAgreementRequest.builder()
                .personal_information_agreement(true)
                .build();
        memberRepository.save(member);

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateAgreement(new MemberAuthDto(99L), memberAgreementRequest));
        assertThrows(InvalidRequestException.class, () -> memberService.updateAgreement(new MemberAuthDto(99L), memberAgreementRequest2));
        assertThrows(InvalidRequestException.class, () -> memberService.updateAgreement(new MemberAuthDto(99L), memberAgreementRequest3));
    }


    @Test
    @DisplayName("약관 동의 테스트 - 요청된 값을 기반으로 Member를 업데이트 한다")
    void agreeTerms2() {
        // given
        MemberAgreementRequest memberAgreementRequest = MemberAgreementRequest.builder()
                .marketing_agreement(true)
                .personal_information_agreement(false)
                .build();
        Member member = memberRepository.save(Member.builder().build());

        // when
        Member res = memberService.updateAgreement(new MemberAuthDto(member.getId()), memberAgreementRequest);

        // then
        assertEquals(res.getId(), member.getId());
        assertTrue(res.getMarketingAgreement());
        assertFalse(res.getPersonalInformationAgreement());
    }

    @Test
    @DisplayName("유저 닉네임 설정 - 닉네임이 중복되면 400 에러 반환한다.")
    void setNickname() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        Member member2 = memberRepository.save(Member.builder()
                .build());

        // when & then
        memberService.updateName(new MemberAuthDto(member.getId()), new MemberNameRequest("nickname"));
        assertThrows(InvalidRequestException.class, () -> memberService.updateName(new MemberAuthDto(member2.getId()), new MemberNameRequest("nickname")));
    }

    @Test
    @DisplayName("유저 닉네임 설정 - 최대 10자 이하로 설정 가능, 아니면 400 에러 반환한다.")
    void setNickname2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateName(new MemberAuthDto(member.getId()), new MemberNameRequest("nickname1234567890")));
    }

    @Test
    @DisplayName("유저 닉네임 설정 - 최소 1글자 이상으로 설정 가능, 아니면 400 에러 반환한다.")
    void setNickname3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateName(new MemberAuthDto(member.getId()), new MemberNameRequest("")));
        assertDoesNotThrow(() -> memberService.updateName(new MemberAuthDto(member.getId()), new MemberNameRequest("n")));
    }


    @Test
    @DisplayName("유저 닉네임 설정 - 성공시 Member 업데이트한다.")
    void setNickname4() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        // when
        member = memberService.updateName(new MemberAuthDto(member.getId()), new MemberNameRequest("nickname"));

        // then
        assertEquals(member.getId(), member.getId());
        assertEquals("nickname", member.getName());
    }

    @Test
    @DisplayName("선호 지역 설정 테스트 - 성공시 Member 업데이트한다.")
    void updatePreferenceLocation() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest memberPreferenceRegionRequest = MemberPreferenceRegionRequest.builder()
                .location_ids(List.of(1L, 2L, 3L))
                .build();

        // when
        Member result = memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), memberPreferenceRegionRequest);

        // then
        assertEquals(member.getId(), result.getId());

        List<MemberPreference> memberPreferenceRegions = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.REGION);
        assertEquals(3, memberPreferenceRegions.size());
    }


    @Test
    @DisplayName("선호 지역 설정 테스트 - 범위에 없는 지역인 경우 400 에러를 반환한다.")
    void updatePreferenceLocation2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest memberPreferenceRegionRequest = MemberPreferenceRegionRequest.builder()
                .location_ids(List.of(1L, 2L, 99L))
                .build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), memberPreferenceRegionRequest));
    }

    @Test
    @DisplayName("선호 지역 설정 테스트 - 지역을 선택하지 않은 경우 400 에러를 반환한다.")
    void updatePreferenceLocation3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest memberPreferenceRegionRequest = MemberPreferenceRegionRequest.builder().build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), memberPreferenceRegionRequest));
    }

    @Test
    @DisplayName("업무 목적 설정 테스트 - 업무 목적을 선택하지 않은 경우 400 에러를 반환한다.")
    void updatePurpose() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updatePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest));
    }

    @Test
    @DisplayName("업무 목적 설정 테스트 - 선택한 업무 목적이 범위에 없는 경우 400 에러를 반환한다.")
    void updatePurpose2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 99L)).build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updatePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest));
    }

    @Test
    @DisplayName("업무 목적 설정 테스트 - 성공시 Member를 업데이트한다.")
    void updatePurpose3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 3L)).build();

        // when
        Member result = memberService.updatePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest);

        // then
        assertEquals(member.getId(), result.getId());

        List<MemberPreference> memberPurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.PURPOSE);
        assertEquals(3, memberPurposes.size());
    }

    @Test
    @DisplayName("선호 워크 스페이스 설정 테스트 - 선호 워크 스페이스를 선택하지 않은 경우 400 에러를 반환한다.")
    void updatePreferenceWorkspace() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateWorkspacePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest));
    }

    @Test
    @DisplayName("선호 워크 스페이스 설정 테스트 - 선택한 선호 워크 스페이스가 범위에 없는 경우 400 에러를 반환한다.")
    void updatePreferenceWorkspace2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 99L)).build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateWorkspacePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest));
    }

    @Test
    @DisplayName("선호 워크 스페이스 설정 테스트 - 선택한 선호 워크 스페이스가 3개보다 많은 경우 400 에러를 반환한다.")
    void updatePreferenceWorkspace3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 3L, 4L)).build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> memberService.updateWorkspacePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest));
    }

    @Test
    @DisplayName("선호 워크 스페이스 설정 테스트 - 성공시 Member를 업데이트한다.")
    void updatePreferenceWorkspace4() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPurposeRequest memberPurposeRequest = MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 3L)).build();

        // when
        Member result = memberService.updateWorkspacePurpose(new MemberAuthDto(member.getId()), memberPurposeRequest);

        // then
        assertEquals(member.getId(), result.getId());

        List<MemberPreference> memberPurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.WORKSPACE_CATEGORY);
        assertEquals(3, memberPurposes.size());
    }

    @Test
    @DisplayName("온보딩 진행 상황 조회 테스트 - 아무런 온보딩을 진행하지 않은 경우")
    void getOnboardingStatus() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when
        MemberOnboardingResponse result = memberService.getOnboardingStatus(new MemberAuthDto(member.getId()));

        // then
        assertFalse(result.getTerms_of_service_status());
        assertFalse(result.getNickname_status());
        assertFalse(result.getMember_preference_region_status());
        assertFalse(result.getMember_purpose_status());
        assertFalse(result.getMember_preference_workspace_status());
    }

    @Test
    @DisplayName("온보딩 진행 상황 조회 테스트 - 약관 동의만 진행한 경우")
    void getOnboardingStatus2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        memberService.updateAgreement(new MemberAuthDto(member.getId()), MemberAgreementRequest.builder().marketing_agreement(true).personal_information_agreement(true).build());

        // when
        MemberOnboardingResponse result = memberService.getOnboardingStatus(new MemberAuthDto(member.getId()));

        // then
        assertTrue(result.getTerms_of_service_status());
        assertFalse(result.getNickname_status());
        assertFalse(result.getMember_preference_region_status());
        assertFalse(result.getMember_purpose_status());
        assertFalse(result.getMember_preference_workspace_status());
    }

    @Test
    @DisplayName("온보딩 진행 상황 조회 테스트 - 약관 동의와 닉네임 설정을 진행한 경우")
    void getOnboardingStatus3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        memberService.updateAgreement(new MemberAuthDto(member.getId()), MemberAgreementRequest.builder().marketing_agreement(true).personal_information_agreement(true).build());
        memberService.updateName(new MemberAuthDto(member.getId()), MemberNameRequest.builder().name("test").build());

        // when
        MemberOnboardingResponse result = memberService.getOnboardingStatus(new MemberAuthDto(member.getId()));

        // then
        assertTrue(result.getTerms_of_service_status());
        assertTrue(result.getNickname_status());
        assertFalse(result.getMember_preference_region_status());
        assertFalse(result.getMember_purpose_status());
        assertFalse(result.getMember_preference_workspace_status());
    }

    @Test
    @DisplayName("온보딩 진행 상황 조회 테스트 - 모든 온보딩을 진행한 경우")
    void getOnboardingStatus4() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        memberService.updateAgreement(new MemberAuthDto(member.getId()), MemberAgreementRequest.builder().marketing_agreement(true).personal_information_agreement(true).build());
        memberService.updateName(new MemberAuthDto(member.getId()), MemberNameRequest.builder().name("test").build());
        memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), MemberPreferenceRegionRequest.builder().location_ids(List.of(1L, 2L, 3L)).build());
        memberService.updatePurpose(new MemberAuthDto(member.getId()), MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 3L)).build());
        memberService.updateWorkspacePurpose(new MemberAuthDto(member.getId()), MemberPurposeRequest.builder().purpose_ids(List.of(1L, 2L, 3L)).build());

        // when
        MemberOnboardingResponse result = memberService.getOnboardingStatus(new MemberAuthDto(member.getId()));

        // then
        assertTrue(result.getTerms_of_service_status());
        assertTrue(result.getNickname_status());
        assertTrue(result.getMember_preference_region_status());
        assertTrue(result.getMember_purpose_status());
        assertTrue(result.getMember_preference_workspace_status());
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 등록 테스트 - 스크랩을 성공하는 경우")
    void scrapWorkspace() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when
        ApiStatusResponse apiStatusResponse = memberService.addStar(
            new MemberAuthDto(member.getId()), 1L);

        // then
        assertEquals(apiStatusResponse.getStatus(), HttpStatus.OK.value());
        assertEquals(apiStatusResponse.getMessage(), "스크랩을 성공하였습니다.");
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 등록 테스트 - 존재하지 않는 워크스페이스를 스크랩하는 경우 404 예외 발생")
    void scrapWorkspace2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when & then
        assertThrows(WorkspaceNotFoundException.class, () -> {
            memberService.addStar(new MemberAuthDto(member.getId()), 100L);
        });
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 삭제 테스트 - 스크랩 삭제를 성공하는 경우")
    void scrapWorkspaceDelete() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when
        ApiStatusResponse apiStatusResponse = memberService.addStar(
            new MemberAuthDto(member.getId()), 1L);
        ApiStatusResponse apiStatusResponse2 = memberService.removeStar(
            new MemberAuthDto(member.getId()), 1L);

        // then
        assertEquals(apiStatusResponse.getStatus(), HttpStatus.OK.value());
        assertEquals(apiStatusResponse.getMessage(), "스크랩을 성공하였습니다.");
        assertEquals(apiStatusResponse2.getStatus(), HttpStatus.OK.value());
        assertEquals(apiStatusResponse2.getMessage(), "스크랩 삭제를 성공하였습니다.");
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 등록 테스트 - 스크랩을 중복 등록하는 경우")
    void scrapDuplicateWorkspace() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        memberService.addStar(new MemberAuthDto(member.getId()), 1L);

        // when
        ApiStatusResponse apiStatusResponse = memberService.addStar(
            new MemberAuthDto(member.getId()), 1L);

        // then
        assertEquals(apiStatusResponse.getStatus(), HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 삭제 테스트 - 존재하지 않는 스크랩을 삭제하는 경우 404 예외 발생")
    void scrapWorkspaceDelete2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        // when & then
        assertThrows(WorkspaceNotFoundException.class, () -> {
            memberService.removeStar(new MemberAuthDto(member.getId()), 100L);
        });
    }

    @Test
    @DisplayName("회원 워크스페이스 스크랩 리스트 조회 테스트 - 스크랩 조회를 성공하는 경우")
    void getScrapWorkspaceList() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());

        memberService.addStar(new MemberAuthDto(member.getId()), 1L);
        memberService.addStar(new MemberAuthDto(member.getId()), 2L);
        memberService.addStar(new MemberAuthDto(member.getId()), 3L);

        // when
        MemberScrapListResponse starList = memberService.getStarList(
            new MemberAuthDto(member.getId()));

        // then
        assertEquals(starList.getList().size(), 3);
    }

    @Test
    @DisplayName("회원 온보딩 진행 상황 업데이트 테스트 - 모든 온보딩을 정상적으로 진행한 경우")
    void updateOnboardingStatus() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberOnboardingRequest request = MemberOnboardingRequest.builder()
            .purpose_ids(Set.of(1, 2, 3))
            .workspace_purpose_ids(Set.of(1, 2, 3))
            .marketing_agreement(true)
            .personal_information_agreement(true)
            .location_ids(Set.of(1, 2, 3))
            .name("test")
            .build();


        // when
        ApiStatusResponse apiStatusResponse = memberService.updateOnboardingStatus(
            new MemberAuthDto(member.getId()), request);

        // then
        assertEquals(apiStatusResponse.getStatus(), HttpStatus.OK.value());

        Member resultMember = memberRepository.findById(member.getId()).get();
        assertEquals(resultMember.getName(), "test");

        MemberPreferencesResponse preferences = memberService.getPreferences(
            new MemberAuthDto(member.getId()));
        assertEquals(preferences.getPreference_workspace_purposes().size(), 3);
        assertEquals(preferences.getPreference_workspace_regions().size(), 3);
        assertEquals(preferences.getPreference_workspace_types().size(), 3);
    }

    @Test
    @DisplayName("회원 온보딩 진행 상황 업데이트 테스트 - 이름의 길이가 1자 미만인 경우 400 에러를 되돌려준다")
    void updateOnboardingStatus2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberOnboardingRequest request = MemberOnboardingRequest.builder()
            .purpose_ids(Set.of(1, 2, 3))
            .workspace_purpose_ids(Set.of(1, 2, 3))
            .marketing_agreement(true)
            .personal_information_agreement(true)
            .location_ids(Set.of(1, 2, 3))
            .name("")
            .build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> {
            memberService.updateOnboardingStatus(
                new MemberAuthDto(member.getId()), request);
        });
    }

    @Test
    @DisplayName("회원 온보딩 진행 상황 업데이트 테스트 - 이름의 길이가 20자를 초과하는 경우 400 에러를 되돌려준다")
    void updateOnboardingStatus3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberOnboardingRequest request = MemberOnboardingRequest.builder()
            .purpose_ids(Set.of(1, 2, 3))
            .workspace_purpose_ids(Set.of(1, 2, 3))
            .marketing_agreement(true)
            .personal_information_agreement(true)
            .location_ids(Set.of(1, 2, 3))
            .name("123456789012345678901")
            .build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> {
            memberService.updateOnboardingStatus(
                new MemberAuthDto(member.getId()), request);
        });
    }

    @Test
    @DisplayName("회원 온보딩 진행 상황 업데이트 테스트 - 선호 워크 스페이스 타입이 4개 이상인 경우 400 에러를 되돌려준다")
    void updateOnboardingStatus4() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberOnboardingRequest request = MemberOnboardingRequest.builder()
            .purpose_ids(Set.of(1, 2, 3))
            .workspace_purpose_ids(Set.of(1, 2, 3, 4))
            .marketing_agreement(true)
            .personal_information_agreement(true)
            .location_ids(Set.of(1, 2, 3))
            .name("test")
            .build();

        // when & then
        assertThrows(InvalidRequestException.class, () -> {
            memberService.updateOnboardingStatus(
                new MemberAuthDto(member.getId()), request);
        });
    }

    @Test
    @DisplayName("회원의 선호 지역 변경 - 선호 지역을 정상적으로 변경하는 경우")
    void updateLocation() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest request = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L, 3L))
            .build();

        // when
        Member resultMember = memberService.updatePreferenceLocation(
            new MemberAuthDto(member.getId()), request);

        // then
        assertNotNull(resultMember);

        MemberPreferencesResponse preferences = memberService.getPreferences(
            new MemberAuthDto(member.getId()));
        assertEquals(preferences.getPreference_workspace_regions().size(), 3);
    }

    @Test
    @DisplayName("회원의 선호 지역 변경 - 변경된 지역에 중복이 있는 경우 중복을 제거 후 업데이트를 진행한다.")
    void updateLocation2() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest request = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L, 2L))
            .build();

        // when
        Member resultMember = memberService.updatePreferenceLocation(
            new MemberAuthDto(member.getId()), request);
        Set<MemberPreference> preferences = memberPreferenceRepository.findAllByMember(resultMember);

        // then
        assertNotNull(resultMember);
        assertEquals(preferences.size(), 2);

    }

    @Test
    @DisplayName("회원의 선호 지역 변경 - 변경된 지역이 추가 되는 경우")
    void updateLocation3() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest request = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L))
            .build();
        MemberPreferenceRegionRequest request2 = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L, 3L))
            .build();

        // when
        memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), request);
        Member resultMember = memberService.updatePreferenceLocation(
            new MemberAuthDto(member.getId()), request2);

        // then
        assertNotNull(resultMember);

        MemberPreferencesResponse preferences = memberService.getPreferences(
            new MemberAuthDto(member.getId()));
        assertEquals(preferences.getPreference_workspace_regions().size(), 3);
    }

    @Test
    @DisplayName("회원의 선호 지역 변경 - 변경된 지역이 삭제 되는 경우")
    void updateLocation4() {
        // given
        Member member = memberRepository.save(Member.builder()
                .build());
        MemberPreferenceRegionRequest request = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L, 3L))
            .build();
        MemberPreferenceRegionRequest request2 = MemberPreferenceRegionRequest.builder()
            .location_ids(List.of(1L, 2L))
            .build();

        // when
        memberService.updatePreferenceLocation(new MemberAuthDto(member.getId()), request);
        Member resultMember = memberService.updatePreferenceLocation(
            new MemberAuthDto(member.getId()), request2);

        // then
        assertNotNull(resultMember);

        MemberPreferencesResponse preferences = memberService.getPreferences(
            new MemberAuthDto(member.getId()));
        assertEquals(preferences.getPreference_workspace_regions().size(), 2);
    }
}
