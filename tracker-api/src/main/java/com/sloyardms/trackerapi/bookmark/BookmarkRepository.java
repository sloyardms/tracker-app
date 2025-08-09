package com.sloyardms.trackerapi.bookmark;

import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    Page<Bookmark> findAllByUserUuid(UUID userUuid, Pageable pageable);

}
