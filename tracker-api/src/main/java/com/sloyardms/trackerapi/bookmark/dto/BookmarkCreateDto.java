package com.sloyardms.trackerapi.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkCreateDto {

    private UUID groupUuid;
    private String title;
    private String url;
    private String description;
    private boolean favorite;

}
