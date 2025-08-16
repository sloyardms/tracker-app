package com.sloyardms.trackerapi.note.exception;

import java.util.UUID;

public class NoteNotFoundException extends RuntimeException {

    public NoteNotFoundException(final UUID uuid) {
        super("Note with UUID " + uuid + " not found");
    }

    public NoteNotFoundException(final UUID uuid, Throwable e) {
        super("Note with UUID " + uuid + " not found", e);
    }

}
