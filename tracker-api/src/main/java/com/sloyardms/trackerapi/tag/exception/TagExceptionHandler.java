package com.sloyardms.trackerapi.tag.exception;

import com.sloyardms.trackerapi.common.exception.ProblemDetailUtil;
import com.sloyardms.trackerapi.tag.TagController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = TagController.class)
public class TagExceptionHandler {

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(TagNotFoundException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "Tag not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(TagNameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(TagNameAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Tag name already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
