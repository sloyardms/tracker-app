package com.sloyardms.trackerapi.note;

import com.sloyardms.trackerapi.bookmark.entity.Bookmark;
import com.sloyardms.trackerapi.note.dto.NoteCreateDto;
import com.sloyardms.trackerapi.note.dto.NoteDto;
import com.sloyardms.trackerapi.note.dto.NoteUpdateDto;
import com.sloyardms.trackerapi.note.entity.Note;
import com.sloyardms.trackerapi.note.exception.NoteNotFoundException;
import com.sloyardms.trackerapi.note.mapper.NoteMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    public NoteService(NoteRepository noteRepository, NoteMapper noteMapper) {
        this.noteRepository = noteRepository;
        this.noteMapper = noteMapper;
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
        }catch (DataIntegrityViolationException e){
            //TODO: log error and return error message
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(UUID uuid) {
        noteRepository.findById(uuid).orElseThrow(() -> new NoteNotFoundException(uuid));
        noteRepository.deleteById(uuid);
    }
}
