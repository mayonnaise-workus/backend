package com.tune.server.service.member;

import com.tune.server.domain.*;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.MemberAgreementRequest;
import com.tune.server.dto.request.MemberNameRequest;
import com.tune.server.dto.request.MemberPreferenceRegionRequest;
import com.tune.server.dto.request.MemberPurposeRequest;
import com.tune.server.exceptions.login.TokenExpiredException;
import com.tune.server.exceptions.member.InvalidRequestException;
import com.tune.server.exceptions.member.MemberNotFoundException;
import com.tune.server.repository.*;
import com.tune.server.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private WorkspacePurposeRepository workspacePurposeRepository;
    private MemberWorkspacePurposeRepository memberWorkspacePurposeRepository;
    private MemberPurposeRepository memberPurposeRepository;
    private PurposeRepository purposeRepository;
    private MemberProviderRepository memberProviderRepository;
    private MemberRepository memberRepository;

    private RegionRepository regionRepository;

    private MemberPreferenceRegionRepository memberPreferenceRegionRepository;

    @Override
    public boolean isExistMember(KakaoUserInfo id) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByProviderIdAndAndProvider(id.getId().toString(), "KAKAO");
        return memberProvider.isPresent();
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
            log.error("회원가입 실패", e);
            return false;
        }
    }

    @Override
    public Member getMember(KakaoUserInfo kakaoUserInfo) {
        Optional<MemberProvider> memberProvider = memberProviderRepository.findByProviderIdAndAndProvider(kakaoUserInfo.getId().toString(), "KAKAO");
        return memberProvider.map(MemberProvider::getMember).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));
    }

    @Override
    @Transactional
    public Map<String, String> refresh(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));
        if (member.getRefreshTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("만료된 토큰입니다.");
        }

        Map<String, String> tokenMap = new HashMap<>();
        Date nextMonth = Date.from(LocalDateTime.now().plusMonths(1).atZone(ZoneId.of("Asia/Seoul")).toInstant());
        // 1개월 미만 남은 경우 refresh_token 을 추가한다.
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
            throw new InvalidRequestException("필수 파라미터가 누락되었습니다.");
        }

        Member memberEntity = memberRepository.findById(member.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));
        memberEntity.setMarketingAgreement(request.getMarketing_agreement());
        memberEntity.setPersonalInformationAgreement(request.getPersonal_information_agreement());
        return memberRepository.save(memberEntity);
    }

    @Override
    public Member getInfo(MemberAuthDto member) {
        return memberRepository.findById(member.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));
    }

    @Override
    @Transactional
    public Member updateName(MemberAuthDto principal, MemberNameRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));

        if (request.getName() == null) {
            throw new InvalidRequestException("필수 파라미터가 누락되었습니다.");
        } else if (request.getName().length() > 10) {
            throw new InvalidRequestException("이름은 10자 이하로 입력해주세요.");
        }  else if (request.getName().length() < 1) {
            throw new InvalidRequestException("이름은 1자 이상으로 입력해주세요.");
        } else if (memberRepository.findByName(request.getName()).isPresent()) {
            throw new InvalidRequestException("이미 존재하는 이름입니다.");
        }

        member.setName(request.getName());
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member updatePreferenceLocation(MemberAuthDto principal, MemberPreferenceRegionRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));

        if (request.getLocation_ids() == null || request.getLocation_ids().size() == 0) {
            throw new InvalidRequestException("필수 파라미터가 누락되었습니다.");
        }

        Set<MemberPreferenceRegion> memberPreferenceRegions = new HashSet<>();
        for (Long locationId : request.getLocation_ids()) {
            MemberPreferenceRegion memberPreferenceRegion = MemberPreferenceRegion.builder()
                    .region(regionRepository.findById(locationId).orElseThrow(() -> new InvalidRequestException("존재하지 않는 지역입니다.")))
                    .member(member)
                    .build();
            memberPreferenceRegions.add(memberPreferenceRegion);
        }

        member.setMemberPreferenceRegion(memberPreferenceRegions);
        memberPreferenceRegionRepository.saveAll(memberPreferenceRegions);
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member updatePurpose(MemberAuthDto principal, MemberPurposeRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));

        if (request.getPurpose_ids() == null || request.getPurpose_ids().size() == 0) {
            throw new InvalidRequestException("필수 파라미터가 누락되었습니다.");
        }

        Set<MemberPurpose> memberPurposes = new HashSet<>();
        for(Long purposeId: request.getPurpose_ids()) {
            MemberPurpose memberPurpose = purposeRepository.findById(purposeId).map(purpose -> MemberPurpose.builder()
                    .purpose(purpose)
                    .member(member)
                    .build()).orElseThrow(() -> new InvalidRequestException("존재하지 않는 목적입니다."));
            memberPurposes.add(memberPurpose);
        }

        member.setMemberPurpose(memberPurposes);
        memberPurposeRepository.saveAll(memberPurposes);
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member updateWorkspacePurpose(MemberAuthDto principal, MemberPurposeRequest request) {
        Member member = memberRepository.findById(principal.getId()).orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 없습니다."));

        if (request.getPurpose_ids() == null || request.getPurpose_ids().size() == 0) {
            throw new InvalidRequestException("필수 파라미터가 누락되었습니다.");
        } else if (request.getPurpose_ids().size() > 3) {
            throw new InvalidRequestException("최대 3개의 선택지를 선택할 수 있습니다.");
        }

        Set<MemberWorkspacePurpose> memberWorkspacePurposes = new HashSet<>();
        for(Long id: request.getPurpose_ids()) {
            MemberWorkspacePurpose memberWorkspacePurpose = workspacePurposeRepository.findById(id).map(purpose -> MemberWorkspacePurpose.builder()
                    .purpose(purpose)
                    .member(member)
                    .build()).orElseThrow(() -> new InvalidRequestException("존재하지 않는 목적입니다."));
            memberWorkspacePurposes.add(memberWorkspacePurpose);
        }

        member.setMemberWorkSpacePurpose(memberWorkspacePurposes);
        memberWorkspacePurposeRepository.saveAll(memberWorkspacePurposes);
        return memberRepository.save(member);
    }

}
