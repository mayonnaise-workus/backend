package com.tune.server.service.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.tune.server.domain.*;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.apple.AppleAuthTokenDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.*;
import com.tune.server.dto.response.ApiStatusResponse;
import com.tune.server.dto.response.MemberOnboardingResponse;
import com.tune.server.dto.response.MemberPreferencesResponse;
import com.tune.server.dto.response.MemberScrapListResponse;
import com.tune.server.dto.response.WorkspaceListResponse;
import com.tune.server.enums.MemberPreferenceEnum;
import com.tune.server.enums.TagTypeEnum;
import com.tune.server.enums.WorkspaceTagEnum;
import com.tune.server.exceptions.login.TokenExpiredException;
import com.tune.server.exceptions.member.InvalidRequestException;
import com.tune.server.exceptions.member.MemberNotFoundException;
import com.tune.server.repository.*;
import com.tune.server.service.AuthService;
import com.tune.server.service.workspace.WorkspaceService;
import com.tune.server.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private TagRepository tagRepository;
    private MemberProviderRepository memberProviderRepository;
    private MemberRepository memberRepository;
    private MemberPreferenceRepository memberPreferenceRepository;
    private MemberScrapRepository memberScrapRepository;
    private WorkspaceService workspaceService;

    private WorkspaceTagRepository workspaceTagRepository;
    private AuthService authService;

    @Override
    public boolean isExistMember(KakaoUserInfo id) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByProviderIdAndAndProvider(id.getId().toString(), "KAKAO");
        return memberProvider.isPresent();
    }

    @Override
    public boolean isExistMember(AppleAuthTokenDto appleAuthTokenDto) {
        System.out.println(appleAuthTokenDto.getUser_id());
        return memberProviderRepository.findByProviderIdAndAndProvider(appleAuthTokenDto.getUser_id(), "APPLE").isPresent();
    }

    @Override
    public boolean isExistMember(GoogleLoginRequest googleLoginRequest) {
        return memberProviderRepository.findByProviderIdAndAndProvider(googleLoginRequest.getId(), "GOOGLE").isPresent();
    }

    @Override
    @Transactional
    public boolean signUp(KakaoUserInfo kakaoUserInfo) {
        try {
            Member member = Member
                    .builder()
                    .refreshToken(JwtUtil.generateRefreshToken())
                    .refreshTokenExpiresAt(LocalDateTime.now().plusMonths(JwtUtil.REFRESH_TOKEN_EXPIRES_MONTH))
                    .build();

            MemberProvider memberProvider = MemberProvider.builder()
                    .providerId(kakaoUserInfo.getId().toString())
                    .refreshToken(kakaoUserInfo.getRefreshToken())
                    .member(member)
                    .provider("KAKAO")
                    .build();

            memberRepository.save(member);
            memberProviderRepository.save(memberProvider);
            return true;
        } catch (Exception e) {
            log.error("???????????? ??????", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean signUp(AppleAuthTokenDto appleAuthTokenDto) {
        try {
            Member member = Member
                    .builder()
                    .refreshToken(JwtUtil.generateRefreshToken())
                    .refreshTokenExpiresAt(LocalDateTime.now().plusMonths(JwtUtil.REFRESH_TOKEN_EXPIRES_MONTH))
                    .build();

            MemberProvider memberProvider = MemberProvider.builder()
                    .providerId(appleAuthTokenDto.getUser_id())
                    .refreshToken(appleAuthTokenDto.getRefresh_token())
                    .member(member)
                    .provider("APPLE")
                    .build();

            memberRepository.save(member);
            memberProviderRepository.save(memberProvider);
            return true;
        } catch (Exception e) {
            log.error("???????????? ??????", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean signUp(GoogleLoginRequest request, GoogleTokenResponse tokenResponse) {
        try {
            Member member = Member
                    .builder()
                    .refreshToken(JwtUtil.generateRefreshToken())
                    .refreshTokenExpiresAt(LocalDateTime.now().plusMonths(JwtUtil.REFRESH_TOKEN_EXPIRES_MONTH))
                    .build();

            MemberProvider memberProvider = MemberProvider.builder()
                    .providerId(request.getId())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .member(member)
                    .provider("GOOGLE")
                    .build();

            memberRepository.save(member);
            memberProviderRepository.save(memberProvider);
            return true;
        } catch (Exception e) {
            log.error("???????????? ??????", e);
            return false;
        }
    }

    @Override
    public Member getMember(KakaoUserInfo kakaoUserInfo) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByProviderIdAndAndProvider(kakaoUserInfo.getId().toString(), "KAKAO");
        return memberProvider.map(MemberProvider::getMember).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
    }

    @Override
    public Member getMember(AppleAuthTokenDto appleAuthTokenDto) {
        return memberProviderRepository.findByProviderIdAndAndProvider(appleAuthTokenDto.getUser_id(), "APPLE")
                .map(MemberProvider::getMember)
                .orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
    }

    @Override
    public Member getMember(GoogleLoginRequest googleLoginRequest) {
        return memberProviderRepository.findByProviderIdAndAndProvider(googleLoginRequest.getId(), "GOOGLE")
                .map(MemberProvider::getMember)
                .orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
    }

    @Override
    @Transactional
    public Map<String, String> refresh(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
        if (member.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("????????? ???????????????.");
        }

        Map<String, String> tokenMap = new HashMap<>();
        Date nextMonth = Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.of("Asia/Seoul")).toInstant());
        // 1?????? ?????? ?????? ?????? refresh_token ??? ????????????.
        if (member.getRefreshTokenExpiresAt().isBefore(LocalDateTime.ofInstant(nextMonth.toInstant(), ZoneId.of("Asia/Seoul")))) {
            member.setRefreshToken(JwtUtil.generateRefreshToken());
            member.setRefreshTokenExpiresAt(LocalDateTime.now().plusMonths(JwtUtil.REFRESH_TOKEN_EXPIRES_MONTH));
            tokenMap.put("refresh_token", member.getRefreshToken());
        }

        tokenMap.put("access_token", JwtUtil.generateJwt(member));

        return tokenMap;
    }

    @Override
    @Transactional
    public Member updateAgreement(MemberAuthDto member, MemberAgreementRequest request) {
        if (request.getMarketing_agreement() == null || request.getPersonal_information_agreement() == null) {
            throw new InvalidRequestException("?????? ??????????????? ?????????????????????.");
        }

        Member memberEntity = memberRepository.findById(member.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
        memberEntity.setMarketingAgreement(request.getMarketing_agreement());
        memberEntity.setPersonalInformationAgreement(request.getPersonal_information_agreement());
        return memberRepository.save(memberEntity);
    }

    @Override
    public Member getInfo(MemberAuthDto member) {
        return memberRepository.findById(member.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
    }

    @Override
    @Transactional
    public Member updateName(MemberAuthDto principal, MemberNameRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        if (request.getName() == null) {
            throw new InvalidRequestException("?????? ??????????????? ?????????????????????.");
        } else if (request.getName().length() > 10) {
            throw new InvalidRequestException("????????? 10??? ????????? ??????????????????.");
        }  else if (request.getName().length() < 1) {
            throw new InvalidRequestException("????????? 1??? ???????????? ??????????????????.");
        } else if (memberRepository.findByName(request.getName()).isPresent()) {
            throw new InvalidRequestException("?????? ???????????? ???????????????.");
        }

        member.setName(request.getName());
        return memberRepository.save(member);
    }

    @Override
    public Member updatePreferenceLocation(MemberAuthDto principal, MemberPreferenceRegionRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        if (request.getLocation_ids() == null || request.getLocation_ids().size() == 0) {
            throw new InvalidRequestException("?????? ??????????????? ?????????????????????.");
        }

        for (Long locationId : request.getLocation_ids()) {
            try {
                memberPreferenceRepository.save(
                    MemberPreference.builder()
                        .type(MemberPreferenceEnum.REGION)
                        .member(member)
                        .tag(tagRepository.findTagByTypeAndTagId(TagTypeEnum.REGION, locationId).orElseThrow(() -> new InvalidRequestException("???????????? ?????? ???????????????.")))
                        .build()
                );
            } catch (InvalidRequestException e) {
                throw new InvalidRequestException(e.getMessage());
            } catch (Exception ignored) {}
        }
        return memberRepository.save(member);
    }

    @Override
    public Member updatePurpose(MemberAuthDto principal, MemberPurposeRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        if (request.getPurpose_ids() == null || request.getPurpose_ids().size() == 0) {
            throw new InvalidRequestException("?????? ??????????????? ?????????????????????.");
        }

        for(Long purposeId: request.getPurpose_ids()) {
            try {
                memberPreferenceRepository.save(
                    MemberPreference.builder()
                        .type(MemberPreferenceEnum.PURPOSE)
                        .member(member)
                        .tag(tagRepository.findTagByTypeAndTagId(TagTypeEnum.PURPOSE, purposeId).orElseThrow(() -> new InvalidRequestException("???????????? ?????? ???????????????.")))
                        .build()
                );
            } catch (InvalidRequestException e) {
                throw new InvalidRequestException(e.getMessage());
            } catch (Exception ignored) {}
        }

        return memberRepository.save(member);
    }

    @Override
    public Member updateWorkspacePurpose(MemberAuthDto principal, MemberPurposeRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        if (request.getPurpose_ids() == null || request.getPurpose_ids().size() == 0) {
            throw new InvalidRequestException("?????? ??????????????? ?????????????????????.");
        } else if (request.getPurpose_ids().size() > 3) {
            throw new InvalidRequestException("?????? 3?????? ???????????? ????????? ??? ????????????.");
        }

        for(Long id: request.getPurpose_ids()) {
            try {
                memberPreferenceRepository.save(
                    MemberPreference.builder()
                        .type(MemberPreferenceEnum.WORKSPACE_CATEGORY)
                        .member(member)
                        .tag(tagRepository.findTagByTypeAndTagId(TagTypeEnum.CATEGORY, id).orElseThrow(() -> new InvalidRequestException("???????????? ?????? ???????????????.")))
                        .build()
                );
            } catch (InvalidRequestException e) {
                throw new InvalidRequestException(e.getMessage());
            } catch (Exception ignored) {}
        }

        return memberRepository.save(member);
    }

    @Override
    public MemberOnboardingResponse getOnboardingStatus(MemberAuthDto principal) {
        MemberOnboardingResponse response = new MemberOnboardingResponse();
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        response.setNickname_status(member.getName() != null);
        response.setTerms_of_service_status(member.getMarketingAgreement() != null && member.getPersonalInformationAgreement() != null);

        List<MemberPreference> memberPurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.PURPOSE);
        response.setMember_purpose_status(memberPurposes.size() > 0);

        List<MemberPreference> memberWorkspacePurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.WORKSPACE_CATEGORY);
        response.setMember_preference_workspace_status(memberWorkspacePurposes.size() > 0);

        List<MemberPreference> memberPreferenceRegions = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.REGION);
        response.setMember_preference_region_status(memberPreferenceRegions.size() > 0);

        return response;
    }

    @Override
    public ApiStatusResponse addStar(MemberAuthDto principal, Long workspaceId) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
        Workspace workspace = workspaceService.findWorkSpaceById(workspaceId);

        try {
            memberScrapRepository.save(
                MemberScrap.builder()
                    .member(member)
                    .workspace(workspace)
                    .build()
            );
        } catch (Exception ignored) {
            return ApiStatusResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("?????? ???????????? ???????????????????????????.")
                .build();
        }

        return ApiStatusResponse.builder()
                .status(HttpStatus.OK.value())
                .message("???????????? ?????????????????????.")
                .build();
    }

    @Override
    @Transactional
    public ApiStatusResponse removeStar(MemberAuthDto principal, Long workspaceId) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
        Workspace workspace = workspaceService.findWorkSpaceById(workspaceId);

        memberScrapRepository.deleteMemberScrapByMemberAndWorkspace(
            member, workspace);

        return ApiStatusResponse.builder()
                .status(HttpStatus.OK.value())
                .message("????????? ????????? ?????????????????????.")
                .build();
    }

    @Override
    public MemberScrapListResponse getStarList(MemberAuthDto principal) {
        List<MemberScrap> memberScraps = memberScrapRepository.findAllByMemberId(principal.getId());
        List<WorkspaceListResponse> workspaceListResponses = new LinkedList<>();

        for (MemberScrap memberScrap : memberScraps) {
            List<WorkspaceTag> workspaceTags = workspaceTagRepository.findAllByWorkspace_Id(
                memberScrap.getWorkspace().getId());
            WorkspaceListResponse response = WorkspaceListResponse.of(memberScrap);

            response.setWorkspace_capacity(workspaceTags.stream()
                .filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.CAPACITY)
                .collect(Collectors.toList()).get(0).getTag().getTagId());

            response.setWorkspace_obj(workspaceTags.stream()
                .filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.PURPOSE)
                .collect(Collectors.toList()).get(0).getTag().getTagId());

            response.setWorkspace_region(workspaceTags.stream()
                .filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.REGION)
                .collect(Collectors.toList()).get(0).getTag().getTagId());

            response.setWorkspace_type(workspaceTags.stream()
                .filter(workspaceTag -> workspaceTag.getType() == WorkspaceTagEnum.CATEGORY)
                .collect(Collectors.toList()).get(0).getTag().getTagId());

            workspaceListResponses.add(response);
        }

        return MemberScrapListResponse.builder()
                .list(workspaceListResponses)
                .build();
    }

    @Override
    public MemberPreferencesResponse getPreferences(MemberAuthDto principal) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));

        List<MemberPreference> memberPurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.PURPOSE);
        List<MemberPreference> memberWorkspacePurposes = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.WORKSPACE_CATEGORY);
        List<MemberPreference> memberPreferenceRegions = memberPreferenceRepository.findAllByMemberAndType(member, MemberPreferenceEnum.REGION);

        return MemberPreferencesResponse.builder()
                .preference_workspace_types(memberWorkspacePurposes.stream().map(MemberPreference::getTag).map(Tag::getTagId).map(Long::intValue).collect(Collectors.toList()))
                .preference_workspace_regions(memberPreferenceRegions.stream().map(MemberPreference::getTag).map(Tag::getTagId).map(Long::intValue).collect(Collectors.toList()))
                .preference_workspace_purposes(memberPurposes.stream().map(MemberPreference::getTag).map(Tag::getTagId).map(Long::intValue).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public ApiStatusResponse deleteMember(MemberAuthDto principal) {
        MemberProvider memberProvider = memberProviderRepository.findByMemberId(principal.getId()).orElseThrow(() -> new MemberNotFoundException("???????????? ????????? ????????????."));
        Member member = memberProvider.getMember();

        // ????????? ??? ?????? ?????? ??????
        boolean deleted = true;
        switch (memberProvider.getProvider()) {
            case "KAKAO":
                deleted = authService.revokeKakaoToken(Long.parseLong(memberProvider.getProviderId()));
                break;
            case "GOOGLE":
                deleted = authService.revokeGoogleToken(memberProvider.getRefreshToken());
                break;
            case "APPLE":
                deleted = authService.revokeAppleToken(memberProvider.getRefreshToken());
                break;

        }
        if (!deleted) {
            return ApiStatusResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("?????? ????????? ?????????????????????.")
                    .build();
        }

        memberScrapRepository.deleteAllByMember(member);
        memberPreferenceRepository.deleteAllByMember(member);
        memberProviderRepository.deleteAllByMember(member);
        memberRepository.delete(member);
        return ApiStatusResponse.builder()
                .status(HttpStatus.OK.value())
                .message("?????? ????????? ?????????????????????.")
                .build();
    }

}
