package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.MemberAgreementRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MemberService {
    boolean isExistMember(KakaoUserInfo kakaoUserInfo);

    boolean signUp(KakaoUserInfo kakaoUserInfo);

    Member getMember(KakaoUserInfo kakaoUserInfo);

    Map<String, String> refresh(String refreshToken);

    Member updateAgreement(MemberAuthDto member, MemberAgreementRequest request);

    Member getInfo(MemberAuthDto principal);
}
