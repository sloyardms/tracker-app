package com.sloyardms.trackerapi.bookmark.exception;

public class BookmarkUrlAlreadyExistsException extends RuntimeException {

    public BookmarkUrlAlreadyExistsException(String url) {
        super(String.format("Bookmark with url %s already exists", url));
    }

}
