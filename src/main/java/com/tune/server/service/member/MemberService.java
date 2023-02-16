package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.dto.kakao.KakaoUserInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MemberService {
    boolean isExistMember(KakaoUserInfo kakaoUserInfo);

    boolean signUp(KakaoUserInfo kakaoUserInfo);

    Member getMember(KakaoUserInfo kakaoUserInfo);

    Map<String, String> refresh(String refreshToken);
}
