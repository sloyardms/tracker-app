package com.sloyardms.trackerapi.repository;

import com.sloyardms.trackerapi.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID>{

    Optional<Tag> findByUserUuidAndName(UUID uuid, String name);

    Page<Tag> findAllByUserUuid(UUID userUuid, Pageable pageable);

    boolean existsByUserUuidAndNameAndUuidNot(UUID userUuid, String name, UUID uuidToExclude);

}
