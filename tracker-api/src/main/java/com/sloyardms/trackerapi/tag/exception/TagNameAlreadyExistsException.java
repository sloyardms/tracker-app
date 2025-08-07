package com.sloyardms.trackerapi.tag.exception;

public class TagNameAlreadyExistsException extends RuntimeException {

    public TagNameAlreadyExistsException(final String tagName) {
        super(String.format("Tag with name %s already exists", tagName));
    }

}
