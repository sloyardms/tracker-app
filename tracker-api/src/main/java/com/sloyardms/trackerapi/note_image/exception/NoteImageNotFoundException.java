package com.sloyardms.trackerapi.note_image.exception;

import java.util.UUID;

public class NoteImageNotFoundException extends RuntimeException {

    public NoteImageNotFoundException(final UUID uuid) {
        super(String.format("Note image with UUID %s not found", uuid));
    }

    public NoteImageNotFoundException(final UUID uuid, Throwable e) {
        super(String.format("Note image with UUID %s not found", uuid), e);
    }

}
