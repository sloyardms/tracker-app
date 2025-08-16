package com.sloyardms.trackerapi.tag.exception;

public class TagNameAlreadyExistsException extends RuntimeException {

    public TagNameAlreadyExistsException(final String tagName) {
        super(String.format("Tag with name %s already exists", tagName));
    }

    public TagNameAlreadyExistsException(final String tagName, Throwable e) {
        super(String.format("Tag with name %s already exists", tagName), e);
    }

}
