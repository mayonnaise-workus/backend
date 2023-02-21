package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberWorkspacePurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberWorkspacePurposeRepository extends JpaRepository<MemberWorkspacePurpose, Long> {
    List<MemberWorkspacePurpose> findAllByMember(Member member);

}
