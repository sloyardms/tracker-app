package com.sloyardms.trackerapi.bookmark.exception;

public class BookmarkUrlAlreadyExistsException extends RuntimeException {

    public BookmarkUrlAlreadyExistsException(final String url) {
        super(String.format("Bookmark with url %s already exists", url));
    }

    public BookmarkUrlAlreadyExistsException(final String url, Throwable e) {
        super(String.format("Bookmark with url %s already exists", url), e);
    }

}
