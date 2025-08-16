package com.sloyardms.trackerapi.note_image.exception;

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
public class NoteImageExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(NoteImageExceptionHandler.class);

    @ExceptionHandler(NoteImageNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoteImageNotFound(NoteImageNotFoundException ex, HttpServletRequest request){
        log.error("NoteImage not found at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "NoteImage not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
