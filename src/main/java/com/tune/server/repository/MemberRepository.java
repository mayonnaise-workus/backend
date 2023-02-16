package com.tune.server.repository;

import com.tune.server.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByRefreshToken(String refreshToken);
}
