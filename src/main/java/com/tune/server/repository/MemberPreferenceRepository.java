package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberPreference;
import com.tune.server.enums.MemberPreferenceEnum;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberPreferenceRepository extends JpaRepository<MemberPreference, Long> {

    List<MemberPreference> findAllByMemberAndType(Member member, MemberPreferenceEnum type);

    void deleteAllByMember(Member member);

    Set<MemberPreference> findAllByMember(Member member);
}
