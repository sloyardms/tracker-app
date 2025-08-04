package com.sloyardms.trackerapi.exception;

public class ResourceDuplicatedException extends RuntimeException{

    public ResourceDuplicatedException(String message) {
        super(message);
    }

    public ResourceDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceDuplicatedException(Throwable cause) {
        super(cause);
    }

}
