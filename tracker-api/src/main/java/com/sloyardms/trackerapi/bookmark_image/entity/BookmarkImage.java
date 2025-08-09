package com.sloyardms.trackerapi.bookmark_image.entity;

import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.common.entity.Auditable;
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
@Table(name = "bookmark_images",indexes = {
        @Index(name = "bookmark_images_bookmark_uuid_index", columnList = "bookmark_uuid")
})
public class BookmarkImage extends Auditable {

    @Id
    @Column(name = "image_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_uuid", nullable = false)
    private Bookmark bookmark;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "thumbnail_mimetype")
    private String thumbnailMimetype;

    @Column(name = "original_image_path")
    private String originalImagePath;

    @Column(name = "original_image_mimetype")
    private String originalImageMimetype;

}
