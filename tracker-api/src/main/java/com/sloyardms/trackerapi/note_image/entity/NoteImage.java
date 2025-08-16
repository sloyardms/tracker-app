package com.sloyardms.trackerapi.note_image.entity;

import com.sloyardms.trackerapi.common.entity.Auditable;
import com.sloyardms.trackerapi.note.entity.Note;
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
@Table(name = "bookmark_notes_images", indexes = {
        @Index(name = "bookmark_notes_images_note_uuid_index", columnList = "note_uuid")
})
public class NoteImage extends Auditable {

    @Id
    @Column(name = "image_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_uuid", nullable = false)
    private Note note;

    @Column(name = "thumbnail_path", nullable = false)
    private String thumbnailPath;

    @Column(name = "thumbnail_mimetype", nullable = false)
    private String thumbnailMimeType;

    @Column(name = "original_image_path")
    private String originalImagePath;

    @Column(name = "original_image_mimetype")
    private String originalImageMimeType;

}
