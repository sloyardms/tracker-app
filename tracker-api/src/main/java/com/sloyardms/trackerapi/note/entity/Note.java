package com.sloyardms.trackerapi.note.entity;

import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.common.entity.Auditable;
import com.sloyardms.trackerapi.note_image.entity.NoteImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmark_notes", indexes = {
    @Index(name = "bookmark_notes_bookmark_uuid_index", columnList = "bookmark_uuid")
})
public class Note extends Auditable {

    @Id
    @Column(name = "note_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_uuid", nullable = false)
    private Bookmark bookmark;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteImage> images = new ArrayList<>();

    public void addImage(NoteImage image){
        image.setNote(this);
        images.add(image);
    }

    public void removeImage(NoteImage image){
        images.remove(image);
        image.setNote(null);
    }

}
