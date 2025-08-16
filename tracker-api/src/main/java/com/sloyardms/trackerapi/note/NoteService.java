package com.sloyardms.trackerapi.note;

import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.common.service.ImageStorageService;
import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.note.dto.NoteUpdateDto;
import com.sloyardms.trackerapi.note.entity.Note;
import com.sloyardms.trackerapi.note.exception.NoteFolderDeletionException;
import com.sloyardms.trackerapi.note.exception.NoteNotFoundException;
import com.sloyardms.trackerapi.note.mapper.NoteMapper;
import com.sloyardms.trackerapi.note_image.NoteImageService;
import com.sloyardms.trackerapi.note_image.dto.NoteImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class NoteService {

    private final static Logger log = LoggerFactory.getLogger(NoteService.class);

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final NoteImageService noteImageService;
    private final ImageStorageService imageStorageService;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper, NoteImageService noteImageService, ImageStorageService imageStorageService) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
        this.noteImageService = noteImageService;
        this.imageStorageService = imageStorageService;
    }

    @Transactional(rollbackFor = Exception.class)
    public NoteDto create(UUID bookmarkUuid, NoteCreateDto noteCreateDto) {
        Note newNote = noteMapper.toEntity(noteCreateDto);

        // Set bookmark and note UUID
        Bookmark bookmark = new Bookmark();
        bookmark.setUuid(bookmarkUuid);
        newNote.setBookmark(bookmark);
        newNote.setUuid(UUID.randomUUID());

        //TODO: handle images next feature
        Note savedNote = saveNoteChanges(newNote);
        return noteMapper.toDto(savedNote);
    }

    @Transactional(readOnly = true)
    public NoteDto getById(UUID uuid) {
        Note note = noteRepository.findById(uuid).orElseThrow(()-> new NoteNotFoundException(uuid));
        return noteMapper.toDto(note);
    }

    @Transactional(readOnly = true)
    public Page<NoteDto> getAllNotesByBookmarkUuid(UUID bookmarkUuid, Pageable pageable) {
        Page<Note> notes = noteRepository.findAllByBookmarkUuid(bookmarkUuid, pageable);
        return notes.map(noteMapper::toDto);
    }

    @Transactional(rollbackFor = Exception.class)
    public NoteDto update(UUID uuid, NoteUpdateDto noteUpdateDto) {
        Note noteDb = noteRepository.findById(uuid).orElseThrow(()-> new NoteNotFoundException(uuid));

        //Set base attributes to update if any
        noteMapper.updateFromDto(noteUpdateDto, noteDb);

        Note savedNote = saveNoteChanges(noteDb);
        return noteMapper.toDto(savedNote);
    }

    private Note saveNoteChanges(Note note) throws DataIntegrityViolationException{
        try {
            return noteRepository.saveAndFlush(note);
        }catch (DataIntegrityViolationException ex){
            String message = "Unexpected error saving bookmark note";
            throw new DataIntegrityViolationException(message, ex);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        Note foundNote = noteRepository.findById(uuid).orElseThrow(() -> new NoteNotFoundException(uuid));
        Bookmark foundBookmark = foundNote.getBookmark();
        UUID bookmarkUuid = foundBookmark.getUuid();
        UUID userUuid = foundBookmark.getUserUuid();

        // Delete note folder
        try {
            imageStorageService.deleteNoteDirectory(userUuid, bookmarkUuid, uuid);
        } catch (Exception e){
            throw new NoteFolderDeletionException(e);
        }

        //Delete the note and note images
        noteImageService.deleteByNoteUuid(uuid);
        noteRepository.deleteById(uuid);
    }

    @Transactional(rollbackFor = Exception.class)
    public NoteImageDto saveNoteImage(UUID noteUuid, MultipartFile imageFile) throws Exception {
        Note foundNote = noteRepository.findById(noteUuid).orElseThrow(()-> new NoteNotFoundException(noteUuid));
        Bookmark foundBookmark = foundNote.getBookmark();
        UUID bookmarkUuid = foundBookmark.getUuid();
        UUID userUuid = foundBookmark.getUserUuid();

        return noteImageService.create(userUuid, bookmarkUuid, noteUuid, imageFile);
    }
}
