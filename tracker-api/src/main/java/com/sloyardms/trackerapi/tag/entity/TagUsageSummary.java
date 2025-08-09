package com.sloyardms.trackerapi.tag.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag_usage_summary")
@IdClass(TagUsageSummaryId.class)
public class TagUsageSummary {

    @Id
    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @Id
    @Column(name = "group_uuid", nullable = false)
    private UUID groupUuid;

    @Id
    @Column(name = "tag_uuid", nullable = false)
    private UUID tagUuid;

    @Column(name = "bookmark_count", nullable = false)
    @Min(0)
    private int bookmarkCount;

}
