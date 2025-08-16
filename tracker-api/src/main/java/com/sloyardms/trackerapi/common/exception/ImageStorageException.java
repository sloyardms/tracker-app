package com.sloyardms.trackerapi.common.exception;

public class ImageStorageException extends RuntimeException {

    public ImageStorageException(final String filename) {
        super("Error storing image " + filename);
    }

    public ImageStorageException(final String filename, Throwable e) {
        super("Error storing image " + filename, e);
    }

}
