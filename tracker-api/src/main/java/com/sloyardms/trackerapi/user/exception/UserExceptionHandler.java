package com.sloyardms.trackerapi.user.exception;

import com.sloyardms.trackerapi.common.exception.ProblemDetailUtil;
import com.sloyardms.trackerapi.user.UserController;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackageClasses = UserController.class)
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.NOT_FOUND,
                "/resource-not-found",
                "User not found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UserIdAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleIdAlreadyExists(UserIdAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "User UUID already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleNameAlreadyExists(UsernameAlreadyExistsException ex, HttpServletRequest request){
        return ProblemDetailUtil.buildProblemDetail(
                HttpStatus.CONFLICT,
                "/resource-already-exists",
                "Username already exists",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

}
