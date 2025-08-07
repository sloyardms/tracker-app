package com.sloyardms.trackerapi.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(final String username) {
        super(String.format("User with username %s already exists", username));
    }

}
