package com.sloyardms.trackerapi.common.exception;

public class ImageDeletionException extends RuntimeException {

    public ImageDeletionException(){
        super("Failed to delete image. Please try again");
    }

    public ImageDeletionException(Throwable e){
        super("Failed to delete image. Please try again", e);
    }

}
