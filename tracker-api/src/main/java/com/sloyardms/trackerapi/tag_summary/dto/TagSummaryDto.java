package com.sloyardms.trackerapi.tag_summary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagSummaryDto {

    private UUID tagUuid;
    private String tagName;
    private int bookmarkCount;

}
