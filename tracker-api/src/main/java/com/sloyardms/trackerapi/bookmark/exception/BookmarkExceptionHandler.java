package com.sloyardms.trackerapi.bookmark.exception;

import com.sloyardms.trackerapi.common.exception.ProblemDetailUtil;
import com.sloyardms.trackerapi.tag.exception.TagNameAlreadyExistsException;
import com.sloyardms.trackerapi.tag.exception.TagNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BookmarkExceptionHandler {

    @ExceptionHandler(BookmarkNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(BookmarkNotFoundException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "Bookmark not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BookmarkTitleAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(BookmarkTitleAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Bookmark title already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BookmarkUrlAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(BookmarkUrlAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Bookmark url already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
