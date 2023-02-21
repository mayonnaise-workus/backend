package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberPreferenceRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberPreferenceRegionRepository extends JpaRepository<MemberPreferenceRegion, Long> {
    List<MemberPreferenceRegion> findAllByMember(Member member);

    Optional<List<MemberPreferenceRegion>> findAllByMemberId(Long memberId);

    List<MemberPreferenceRegion> findAllByMember_Id(Long memberId);
}
