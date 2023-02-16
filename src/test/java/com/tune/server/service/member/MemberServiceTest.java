package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberProvider;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.exceptions.login.TokenExpiredException;
import com.tune.server.exceptions.member.InvalidRequestException;
import com.tune.server.exceptions.member.MemberNotFoundException;
import com.tune.server.repository.MemberProviderRepository;
import com.tune.server.repository.MemberRepository;
import com.tune.server.util.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @BeforeAll
    static void beforeAll() {
        JwtUtil.JWT_SECRET_KEY = "Jo73VnKMoZCBEgBloGffXFTDsZxRZ9fN5geXS3nX0wE";
    }
    @Test
    @DisplayName("소셜 로그인 ID로 회원이 존재하는지 확인하는 테스트 - 존재")
    void isExistMember_success() {
        // given
        Member member = Member.builder()
                .name("test")
                .build();
        MemberProvider memberProvider = MemberProvider.builder()
                .member(member)
                .providerId("99")
                .provider("KAKAO")
                .build();
        memberRepository.save(member);
        memberProviderRepository.save(memberProvider);

        // when & then
        assertTrue(memberService.isExistMember(KakaoUserInfo.builder()
                .id(99L)
                .app_id(99)
                .expires_in(1)
                .build()));
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

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하지 않으면 404 에러 반환")
    void refresh() {
        // given
        String refreshToken = "refresh_token";

        // when & when
        assertThrows(MemberNotFoundException.class, () -> memberService.refresh(refreshToken));
    }

    @Test
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하고 만료까지 1달 이상 남았다면 access_token 토큰 갱신")
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
    @DisplayName("토큰 갱신 테스트 - refresh_token이 존재하고 만료까지 1달 미만 남았다면 refresh_token과 access_token 토큰 갱신")
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
    @DisplayName("토큰 갱신 테스트 - refresh_token이 만료되었다면 401 에러 반환")
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
    @DisplayName("약관 동의 테스트 - 어떠한 매개변수라도 null이면 400 에러 반환")
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
        Member member = Member.builder()
                .id(99L)
                .build();
        MemberAgreementRequest memberAgreementRequest = MemberAgreementRequest.builder()
                .marketing_agreement(true)
                .personal_information_agreement(false)
                .build();
        memberRepository.save(member);

        // when
        member = memberService.updateAgreement(new MemberAuthDto(99L), memberAgreementRequest);

        // then
        assertEquals(99L, member.getId());
        assertTrue(member.getMarketingAgreement());
        assertFalse(member.getPersonalInformationAgreement());
    }
}