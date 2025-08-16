package com.sloyardms.trackerapi.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(final UUID uuid) {
        super(String.format("User with UUID %s not found", uuid));
    }

    public UserNotFoundException(final UUID uuid, Throwable e) {
        super(String.format("User with UUID %s not found", uuid), e);
    }

}
