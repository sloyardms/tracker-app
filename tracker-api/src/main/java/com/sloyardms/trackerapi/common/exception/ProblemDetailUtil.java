package com.sloyardms.trackerapi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ProblemDetailUtil {

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
    public static ResponseEntity<ProblemDetail> buildProblemDetail(HttpStatus status, String typeUri, String title, String detail, String instance) {
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
