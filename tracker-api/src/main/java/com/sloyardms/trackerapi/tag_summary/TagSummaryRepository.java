package com.sloyardms.trackerapi.tag_summary;

import com.sloyardms.trackerapi.tag_summary.entity.TagSummary;
import com.sloyardms.trackerapi.tag_summary.entity.TagSummaryId;
import com.sloyardms.trackerapi.tag_summary.projection.TagUsageProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface TagSummaryRepository extends JpaRepository<TagSummary, TagSummaryId> {

    @Query("""
        SELECT
            t.uuid AS tagUuid,
            t.name AS tagName,
            tus.bookmarkCount AS bookmarkCount
        FROM TagUsageSummary tus 
        JOIN Tag t ON tus.tagUuid = t.uuid 
        WHERE tus.userUuid = :userUuid AND tus.groupUuid = :groupUuid
    """)
    Page<TagUsageProjection> findTagUsageByUserAndGroup(UUID userUuid, UUID groupUuid, Pageable pageable);

}
