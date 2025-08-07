package com.sloyardms.trackerapi.tag;

import com.sloyardms.trackerapi.tag.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>{

    Page<Tag> findAllByUserUuid(UUID userUuid, Pageable pageable);

}
