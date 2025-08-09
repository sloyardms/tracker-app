package com.sloyardms.trackerapi.bookmark.exception;

public class BookmarkTitleAlreadyExistsException extends RuntimeException {

    public BookmarkTitleAlreadyExistsException(String title) {
        super(String.format("Bookmark with title %s already exists", title));
    }

}
