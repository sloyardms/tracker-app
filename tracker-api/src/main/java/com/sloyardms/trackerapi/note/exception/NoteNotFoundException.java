package com.sloyardms.trackerapi.note.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(UUID uuid) {
        super("Note with UUID " + uuid + " not found");
    }

}
