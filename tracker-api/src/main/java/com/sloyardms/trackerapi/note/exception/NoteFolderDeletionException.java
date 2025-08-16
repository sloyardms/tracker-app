package com.sloyardms.trackerapi.note.exception;

public class NoteFolderDeletionException extends RuntimeException {

    public NoteFolderDeletionException() {
        super("Failed to delete folder. Please try again");
    }

    public NoteFolderDeletionException(Throwable e) {
        super("Failed to delete folder. Please try again", e);
    }

}
