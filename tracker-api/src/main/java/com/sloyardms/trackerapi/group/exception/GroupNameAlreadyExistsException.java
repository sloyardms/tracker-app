package com.sloyardms.trackerapi.group.exception;

public class GroupNameAlreadyExistsException extends RuntimeException {

    public GroupNameAlreadyExistsException(final String groupName) {
        super(String.format("Group with name %s already exists", groupName));
    }

}
