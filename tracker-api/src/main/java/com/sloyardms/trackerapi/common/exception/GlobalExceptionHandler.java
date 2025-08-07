package com.sloyardms.trackerapi.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/validation-error",
                "Validation failed",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ProblemDetailUtil.buildProblemDetail(
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

        return ProblemDetailUtil.buildProblemDetail(
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
        return ProblemDetailUtil.buildProblemDetail(
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
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/constraint-violation",
                "Validation failed",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/not-found",
                "No handler found for request",
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.METHOD_NOT_ALLOWED,
                "/method-not-allowed",
                "HTTP method not supported",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException ex, HttpServletRequest request) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/bind-error",
                "Binding failed",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ProblemDetail> handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request) {
        String detail = String.format("Missing path variable: %s", ex.getVariableName());
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "/missing-path-variable",
                "Missing path variable",
                detail,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpServletRequest request) {
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_ACCEPTABLE,
                "/not-acceptable",
                "Media type not acceptable",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "/unsupported-media-type",
                "Media type not supported",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "/internal-server-error",
                "Internal server error",
                "An unexpected error occurred.",
                request.getRequestURI()
        );
    }

}
