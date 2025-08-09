package com.sloyardms.trackerapi.bookmark.exception;

import java.util.UUID;

public class BookmarkNotFoundException extends RuntimeException {

    public BookmarkNotFoundException(UUID uuid) {
        super(String.format("Bookmark with UUID %s not found", uuid));
    }

}
