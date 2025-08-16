package com.sloyardms.trackerapi.note_image.dto;

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
public class NoteImageDto {

    private UUID uuid;
    private String thumbnailPath;
    private String thumbnailMimeType;
    private String originalImagePath;
    private String originalImageMimeType;

}
