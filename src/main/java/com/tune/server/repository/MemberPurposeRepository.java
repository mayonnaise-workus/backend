package com.tune.server.repository;

import com.tune.server.domain.MemberPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPurposeRepository extends JpaRepository<MemberPurpose, Long> {
}
