package com.tune.server.repository;

import com.tune.server.domain.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurposeRepository extends JpaRepository<Purpose, Long> {
}
