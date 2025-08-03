package com.sloyardms.trackerapi.controller.advice;

import com.sloyardms.trackerapi.exception.ResourceDuplicatedException;
import com.sloyardms.trackerapi.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "Resource not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceDuplicatedException.class)
    public ResponseEntity<ProblemDetail> handleDuplicated(ResourceDuplicatedException ex, HttpServletRequest request){
        return buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-duplicated",
                "Resource duplicated",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(BadRequestException ex, HttpServletRequest request){
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/bad-request",
                "Bad request",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request){
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "/internal-server-error",
                "Internal server error",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    /**
     * Builds a standardized ProblemDetail response.
     *
     * @param status HTTP status to return
     * @param typeUri URI identifying the error type documentation
     * @param title Short title for the error
     * @param detail Detailed error message
     * @param instance URI identifying the instance of the error (typically the request URI)
     * @return ResponseEntity containing the ProblemDetail and HTTP status
     */
    private ResponseEntity<ProblemDetail> buildProblemDetail(HttpStatus status, String typeUri, String title, String detail, String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(typeUri));
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setInstance(URI.create(instance));

        return ResponseEntity.status(status)
                .header("Content-Type", "application/problem+json")
                .body(problemDetail);
    }

}
