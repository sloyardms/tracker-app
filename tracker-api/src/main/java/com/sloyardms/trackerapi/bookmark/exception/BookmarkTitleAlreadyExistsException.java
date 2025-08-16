package com.sloyardms.trackerapi.bookmark.exception;

public class BookmarkTitleAlreadyExistsException extends RuntimeException {

    public BookmarkTitleAlreadyExistsException(final String title) {
        super(String.format("Bookmark with title %s already exists", title));
    }

    public BookmarkTitleAlreadyExistsException(final String title, Throwable e) {
        super(String.format("Bookmark with title %s already exists", title), e);
    }

}
