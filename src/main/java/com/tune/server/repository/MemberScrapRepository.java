package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberScrap;
import com.tune.server.domain.Workspace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberScrapRepository extends JpaRepository<MemberScrap, Long> {

    void deleteMemberScrapByMemberAndWorkspace(Member member, Workspace workspace);

    List<MemberScrap> findAllByMemberId(Long id);

    void deleteAllByMember(Member member);
}
