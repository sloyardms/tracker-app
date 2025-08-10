package com.sloyardms.trackerapi.note;

import com.sloyardms.trackerapi.note.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    Page<Note> findAllByBookmarkUuid(UUID bookmarkUuid, Pageable pageable);

}
