package com.sloyardms.trackerapi.tag_summary.entity;

import jakarta.persistence.*;
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
@IdClass(TagSummaryId.class)
@Table(name = "tag_usage_summary")
public class TagSummary {

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
    private int bookmarkCount;

}
