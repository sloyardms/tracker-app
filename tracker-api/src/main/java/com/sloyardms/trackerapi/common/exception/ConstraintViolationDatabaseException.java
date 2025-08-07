package com.sloyardms.trackerapi.common.exception;

public class ConstraintViolationDatabaseException extends RuntimeException{

    public ConstraintViolationDatabaseException(String message) {
        super(message);
    }

    public ConstraintViolationDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintViolationDatabaseException(Throwable cause) {
        super(cause);
    }
}
