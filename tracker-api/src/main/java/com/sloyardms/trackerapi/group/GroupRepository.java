package com.sloyardms.trackerapi.group;

import com.sloyardms.trackerapi.group.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    Optional<Group> findByUserUuidAndName(UUID userUuid, String name);

    Page<Group> findAllByUserUuid(UUID userUuid, Pageable pageable);

    boolean existsByUserUuidAndNameAndUuidNot(UUID userUuid, String name, UUID uuidToExclude);

}
