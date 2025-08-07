package com.sloyardms.trackerapi.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.stream.Collectors;

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

    @ExceptionHandler(ConstraintViolationDatabaseException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(ConstraintViolationDatabaseException ex, HttpServletRequest request){
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/bad-request",
                "Bad request",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/validation-error",
                "Validation failed",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/invalid-json",
                "Malformed JSON request",
                ex.getMostSpecificCause().getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String detail = null;
        if (ex.getRequiredType() != null) {
            detail = String.format("Parameter '%s' must be of type %s", ex.getName(), ex.getRequiredType().getSimpleName());
        }

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/type-mismatch",
                "Parameter type mismatch",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        String detail = String.format("Missing required parameter: %s", ex.getParameterName());

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/missing-param",
                "Missing request parameter",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String detail = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/constraint-violation",
                "Constraint violation",
                detail,
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
