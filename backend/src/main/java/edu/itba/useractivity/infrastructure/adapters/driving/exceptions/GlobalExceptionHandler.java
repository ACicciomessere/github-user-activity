package edu.itba.useractivity.infrastructure.adapters.driving.exceptions;

import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import edu.itba.useractivity.domain.exceptions.InvalidPullRequestActionException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(GitHubClientException.class)
    public ResponseEntity<Map<String, Object>> handleClient(GitHubClientException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(GitHubServerException.class)
    public ResponseEntity<Map<String, Object>> handleServer(GitHubServerException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY, ex);
    }

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(GitHubApiException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ex);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<Map<String, Object>> handleExternalService(ExternalServiceException ex) {
        return buildResponse(HttpStatus.BAD_GATEWAY, ex);
    }

    @ExceptionHandler(InvalidEventTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEventType(InvalidEventTypeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(InvalidPullRequestActionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPullRequestAction(InvalidPullRequestActionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, Exception ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", ex.getMessage()
        );
        return ResponseEntity.status(status).body(body);
    }
}

