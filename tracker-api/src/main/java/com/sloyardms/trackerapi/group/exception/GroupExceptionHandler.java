package com.sloyardms.trackerapi.group.exception;

import com.sloyardms.trackerapi.common.exception.ProblemDetailUtil;
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
public class GroupExceptionHandler {

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(GroupNotFoundException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "Group not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(GroupNameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleGroupNameAlreadyExists(GroupNameAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Group name already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
