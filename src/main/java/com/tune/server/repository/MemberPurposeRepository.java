package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPurposeRepository extends JpaRepository<MemberPurpose, Long> {
    List<MemberPurpose> findAllByMember(Member member);
}
