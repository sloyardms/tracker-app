package com.sloyardms.trackerapi.tag_summary.entity;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TagSummaryId implements Serializable {

    private UUID userUuid;

    private UUID groupUuid;

    private UUID tagUuid;

}
