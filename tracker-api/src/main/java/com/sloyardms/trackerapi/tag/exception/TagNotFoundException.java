package com.sloyardms.trackerapi.tag.exception;

import java.util.UUID;

public class TagNotFoundException extends RuntimeException {

    public TagNotFoundException(final UUID uuid) {
        super(String.format("Tag with UUID %s not found", uuid));
    }

}
