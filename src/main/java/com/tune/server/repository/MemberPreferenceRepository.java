package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberPreference;
import com.tune.server.enums.MemberPreferenceEnum;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemberPreferenceRepository extends JpaRepository<MemberPreference, Long> {

    List<MemberPreference> findAllByMemberAndType(Member member, MemberPreferenceEnum type);

    void deleteAllByMember(Member member);

    Set<MemberPreference> findAllByMember(Member member);

    @Transactional
    @Modifying
    @Query("delete from MemberPreference m where m.member = ?1 and m.type = ?2")
    void deleteAllByMemberAndType(Member member, MemberPreferenceEnum region);
}
