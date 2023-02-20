package com.tune.server.repository;

import com.tune.server.domain.WorkspacePurpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspacePurposeRepository extends JpaRepository<WorkspacePurpose, Long> {
}
