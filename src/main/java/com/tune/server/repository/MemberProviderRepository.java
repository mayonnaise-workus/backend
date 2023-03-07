package com.tune.server.repository;

import com.tune.server.domain.Member;
import com.tune.server.domain.MemberProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {

    Optional<MemberProvider> findByProviderIdAndAndProvider(String providerId, String provider);

    Optional<MemberProvider> findByMember(Member member);

    Optional<MemberProvider> findByMemberId(Long id);
}
