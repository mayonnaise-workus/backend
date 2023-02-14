package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.dto.kakao.KakaoUserInfo;

public interface MemberService {
    boolean isExistMember(KakaoUserInfo kakaoUserInfo);

    boolean signUp(KakaoUserInfo kakaoUserInfo);

    Member getMember(KakaoUserInfo kakaoUserInfo);
}
