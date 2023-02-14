package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberProvider;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.enums.ProviderEnum;
import com.tune.server.repository.MemberProviderRepository;
import com.tune.server.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(MemberServiceImpl.class)
class MemberServiceTest {

    @Autowired
    private MemberProviderRepository memberProviderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;


    @Test
    @DisplayName("소셜 로그인 ID로 회원이 존재하는지 확인하는 테스트 - 존재")
    void isExistMember_success() {
        // given
        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .id(1L)
                .expires_in(1)
                .app_id(1)
                .build();

        // when
        MemberProvider memberProvider = MemberProvider.builder()
                .id(kakaoUserInfo.getId())
                .provider(ProviderEnum.KAKAO)
                .build();
        memberProviderRepository.save(memberProvider);

        // then
        assertTrue(memberService.isExistMember(kakaoUserInfo));
    }

    @Test
    @DisplayName("소셜 로그인 ID로 회원이 존재하는지 확인하는 테스트 - 존재하지 않음")
    void isExistMember_fail() {
        // given
        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .id(99L)
                .expires_in(1)
                .app_id(1)
                .build();

        // then & when
        assertFalse(memberService.isExistMember(kakaoUserInfo));
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signUp() {
        // given
        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .id(1L)
                .expires_in(1)
                .app_id(1)
                .build();

        // when
        boolean result = memberService.signUp(kakaoUserInfo);

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("회원가입 테스트 - 10명")
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
        assertEquals(10, memberRepository.count());
    }

    @Test
    @DisplayName("카카오 유저 정보로 회원 정보를 가져오는 테스트")
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
}