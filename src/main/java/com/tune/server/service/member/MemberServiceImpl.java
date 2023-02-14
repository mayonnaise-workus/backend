package com.tune.server.service.member;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberProvider;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.enums.ProviderEnum;
import com.tune.server.repository.MemberProviderRepository;
import com.tune.server.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {
    private MemberProviderRepository memberProviderRepository;
    private MemberRepository memberRepository;

    @Override
    public boolean isExistMember(KakaoUserInfo id) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByIdAndProvider(id.getId(), ProviderEnum.KAKAO);
        return memberProvider.isPresent();
    }

    @Override
    public boolean signUp(KakaoUserInfo kakaoUserInfo) {
        try {
            Member member = Member.builder()
                    .build();

            MemberProvider memberProvider = MemberProvider.builder()
                    .id(kakaoUserInfo.getId())
                    .member(member)
                    .provider(ProviderEnum.KAKAO)
                    .build();

            memberRepository.save(member);
            memberProviderRepository.save(memberProvider);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Member getMember(KakaoUserInfo kakaoUserInfo) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByIdAndProvider(kakaoUserInfo.getId(), ProviderEnum.KAKAO);
        return memberProvider.map(MemberProvider::getMember).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
    }
}