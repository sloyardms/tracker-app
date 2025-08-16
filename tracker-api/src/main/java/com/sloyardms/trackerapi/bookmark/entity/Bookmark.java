package com.sloyardms.trackerapi.bookmark.entity;

import com.sloyardms.trackerapi.bookmark_image.entity.BookmarkImage;
import com.sloyardms.trackerapi.common.entity.Auditable;
import com.sloyardms.trackerapi.group.entity.Group;
import com.sloyardms.trackerapi.note.entity.Note;
import com.sloyardms.trackerapi.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks", uniqueConstraints = {
        @UniqueConstraint(name = "bookmarks_user_uuid_title_unique", columnNames = {"user_uuid", "title"}),
        @UniqueConstraint(name = "bookmarks_user_uuid_url_unique", columnNames = {"user_uuid", "url"})
},
        indexes = {
                @Index(name = "bookmarks_user_uuid_index", columnList = "user_uuid"),
                @Index(name = "bookmarks_group_uuid_index", columnList = "group_uuid"),
        })
public class Bookmark extends Auditable {

    @Id
    @Column(name = "bookmark_uuid", nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_uuid")
    private Group group;

    @Column(name = "title")
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "favorited")
    private boolean favorite;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "bookmark_tags",
            joinColumns = @JoinColumn(name = "bookmark_uuid", referencedColumnName = "bookmark_uuid"),
            inverseJoinColumns = @JoinColumn(name = "tag_uuid", referencedColumnName = "tag_uuid"))
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bookmark", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "bookmark", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookmarkImage> images = new ArrayList<>();

    public void addNote(Note note){
        note.setBookmark(this);
        notes.add(note);
    }

    public void addImage(BookmarkImage image){
        image.setBookmark(this);
        images.add(image);
    }
}
