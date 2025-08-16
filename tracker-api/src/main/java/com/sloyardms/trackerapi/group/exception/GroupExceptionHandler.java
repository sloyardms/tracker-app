package com.sloyardms.trackerapi.group.exception;

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
public class GroupExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GroupExceptionHandler.class);

    @ExceptionHandler(GroupNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleGroupNotFound(GroupNotFoundException ex, HttpServletRequest request){
        log.error("Group not found at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
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
        log.error("Group name already exists at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Group name already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
