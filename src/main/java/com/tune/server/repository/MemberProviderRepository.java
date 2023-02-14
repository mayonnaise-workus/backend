package com.tune.server.repository;

import com.tune.server.domain.MemberProvider;
import com.tune.server.enums.ProviderEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProviderRepository extends JpaRepository<MemberProvider, Long> {
    Optional<MemberProvider> findByIdAndProvider(Long id, ProviderEnum provider);
}
