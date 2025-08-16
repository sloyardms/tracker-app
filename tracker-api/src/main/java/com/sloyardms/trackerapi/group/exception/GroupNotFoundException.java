package com.sloyardms.trackerapi.group.exception;

import java.util.UUID;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(final UUID uuid) {
        super(String.format("Group with UUID %s not found", uuid));
    }

    public GroupNotFoundException(final UUID uuid, Throwable e) {
        super(String.format("Group with UUID %s not found", uuid), e);
    }

}
