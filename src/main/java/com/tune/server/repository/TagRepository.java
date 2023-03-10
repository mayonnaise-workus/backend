package com.tune.server.repository;

import com.tune.server.domain.Tag;
import com.tune.server.enums.TagTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository  extends JpaRepository<Tag, Long> {
    Optional<Tag> findTagByTypeAndTagId(TagTypeEnum type, Long tagId);

    List<Tag> findAllByType(TagTypeEnum region);
}
