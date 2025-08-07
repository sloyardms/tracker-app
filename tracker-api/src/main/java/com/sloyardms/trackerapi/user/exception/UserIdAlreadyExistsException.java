package com.sloyardms.trackerapi.user.exception;

import java.util.UUID;

public class UserIdAlreadyExistsException extends RuntimeException {

    public UserIdAlreadyExistsException(final UUID userId) {
        super(String.format("User with UUID %s already exists", userId));
    }

}
