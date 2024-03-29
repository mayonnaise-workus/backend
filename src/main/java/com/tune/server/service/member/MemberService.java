package com.tune.server.service.member;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.apple.AppleAuthTokenDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.*;
import com.tune.server.dto.response.ApiStatusResponse;
import com.tune.server.dto.response.MemberOnboardingResponse;
import com.tune.server.dto.response.MemberPreferencesResponse;
import com.tune.server.dto.response.MemberScrapListResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MemberService {
    boolean isExistMember(KakaoUserInfo kakaoUserInfo);
    boolean isExistMember(AppleAuthTokenDto appleAuthTokenDto);

    boolean isExistMember(GoogleLoginRequest googleLoginRequest);
    boolean signUp(KakaoUserInfo kakaoUserInfo);
    boolean signUp(AppleAuthTokenDto appleAuthTokenDto);

    boolean signUp(GoogleLoginRequest request, GoogleTokenResponse tokenResponse);

    Member getMember(KakaoUserInfo kakaoUserInfo);
    Member getMember(AppleAuthTokenDto appleAuthTokenDto);

    Member getMember(GoogleLoginRequest googleLoginRequest);
    Map<String, String> refresh(String refreshToken);

    Member updateAgreement(MemberAuthDto member, MemberAgreementRequest request);

    Member getInfo(MemberAuthDto principal);

    Member updateName(MemberAuthDto principal, MemberNameRequest request);

    Member updatePreferenceLocation(MemberAuthDto principal, MemberPreferenceRegionRequest request);

    Member updatePurpose(MemberAuthDto principal, MemberPurposeRequest request);

    Member updateWorkspacePurpose(MemberAuthDto principal, MemberPurposeRequest request);

    MemberOnboardingResponse getOnboardingStatus(MemberAuthDto principal);

    ApiStatusResponse addStar(MemberAuthDto principal, Long workspaceId);
    
    ApiStatusResponse removeStar(MemberAuthDto principal, Long workspaceId);

    MemberScrapListResponse getStarList(MemberAuthDto principal);

    MemberPreferencesResponse getPreferences(MemberAuthDto principal);

    ApiStatusResponse deleteMember(MemberAuthDto principal);

    ApiStatusResponse validateName(ValidateMemberNameRequest request);

    ApiStatusResponse updateOnboardingStatus(MemberAuthDto principal, MemberOnboardingRequest request);
}
