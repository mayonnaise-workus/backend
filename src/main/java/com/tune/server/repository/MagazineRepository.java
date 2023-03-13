package com.tune.server.repository;

import com.tune.server.domain.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {
}
