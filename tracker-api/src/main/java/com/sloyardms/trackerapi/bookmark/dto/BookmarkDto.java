package com.sloyardms.trackerapi.bookmark.dto;

import com.sloyardms.trackerapi.group.dto.GroupDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {

    private UUID uuid;
    private UUID userUuid;
    private GroupDto group;
    private String title;
    private String url;
    private String description;
    private boolean favorite;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

}
