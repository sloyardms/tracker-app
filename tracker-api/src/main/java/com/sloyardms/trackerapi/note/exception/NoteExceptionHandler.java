package com.sloyardms.trackerapi.note.exception;

import com.sloyardms.trackerapi.common.exception.ProblemDetailUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class NoteExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(NoteExceptionHandler.class);

    @ExceptionHandler(NoteNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteNotFound(NoteNotFoundException ex, HttpServletRequest request){
        log.error("Note not found at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "Note not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NoteFolderDeletionException.class)
    public ResponseEntity<ProblemDetail> handleNoteFolderDeletionException(NoteFolderDeletionException ex, HttpServletRequest request){
        log.error("Error deleting folder at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/folder-deletion-error",
                "Error deleting folder",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
