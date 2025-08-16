package com.sloyardms.trackerapi.note_image;

import com.sloyardms.trackerapi.note_image.entity.NoteImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface NoteImageRepository extends JpaRepository<NoteImage, UUID> {

    List<NoteImage> findAllByNoteUuid(UUID noteUuid);

    @Modifying
    @Transactional
    @Query("DELETE FROM NoteImage ni WHERE ni.note.uuid = :noteUuid")
    void deleteAllByNoteUuid(UUID noteUuid);

}
