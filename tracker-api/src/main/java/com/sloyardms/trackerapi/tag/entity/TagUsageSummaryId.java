package com.sloyardms.trackerapi.tag.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TagUsageSummaryId{

    private UUID userUuid;
    private UUID groupUuid;
    private UUID tagUuid;

}
