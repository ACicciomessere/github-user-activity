package infraestructure.adapters.driving.exceptions;

import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import edu.itba.useractivity.domain.exceptions.InvalidPullRequestActionException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.*;
import edu.itba.useractivity.infrastructure.adapters.driving.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private static void assertBody(ResponseEntity<Map<String, Object>> resp,
                                   HttpStatus expectedStatus,
                                   String expectedError,
                                   String expectedMessageContains) {
        assertThat(resp.getStatusCode()).isEqualTo(expectedStatus);
        Map<String, Object> body = resp.getBody();
        assertThat(body).isNotNull();
        // Claves básicas
        assertThat(body).containsKeys("timestamp", "status", "error", "message");
        // Tipos/valores
        assertThat(body.get("timestamp")).isInstanceOf(LocalDateTime.class);
        assertThat(body.get("status")).isEqualTo(expectedStatus.value());
        assertThat(body.get("error")).isEqualTo(expectedError);
        assertThat(body.get("message").toString()).contains(expectedMessageContains);
    }

    @Test
    @DisplayName("ResourceNotFoundException -> 404 NOT_FOUND")
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User not found");
        ResponseEntity<Map<String, Object>> resp = handler.handleNotFound(ex);
        assertBody(resp, HttpStatus.NOT_FOUND, "Not Found",
                "GitHub API error: User not found (status 404)");
    }

    @Test
    @DisplayName("GitHubClientException -> 400 BAD_REQUEST")
    void handleClient() {
        GitHubClientException ex = new GitHubClientException(400, "Bad Request to GitHub");
        ResponseEntity<Map<String, Object>> resp = handler.handleClient(ex);
        assertBody(resp, HttpStatus.BAD_REQUEST, "Bad Request",
                "GitHub API error: Bad Request to GitHub (status 400)");
    }

    @Test
    @DisplayName("GitHubServerException -> 502 BAD_GATEWAY")
    void handleServer() {
        GitHubServerException ex = new GitHubServerException(503, "Service Unavailable");
        ResponseEntity<Map<String, Object>> resp = handler.handleServer(ex);
        assertBody(resp, HttpStatus.BAD_GATEWAY, "Bad Gateway",
                "GitHub API error: Service Unavailable (status 503)");
    }

    @Test
    @DisplayName("GitHubApiException (genérico) -> 500 INTERNAL_SERVER_ERROR")
    void handleApi() {
        GitHubApiException ex = new GitHubApiException(418, "I'm a teapot");
        ResponseEntity<Map<String, Object>> resp = handler.handleApi(ex);
        assertBody(resp, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "GitHub API error: I'm a teapot (status 418)");
    }

    @Test
    @DisplayName("RateLimitExceededException -> 429 TOO_MANY_REQUESTS")
    void handleRateLimit() {
        RateLimitExceededException ex = new RateLimitExceededException("Rate limit exceeded");
        ResponseEntity<Map<String, Object>> resp = handler.handleRateLimit(ex);
        assertBody(resp, HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests",
                "Rate limit exceeded");
    }

    @Test
    @DisplayName("ExternalServiceException -> 502 BAD_GATEWAY")
    void handleExternalService() {
        ExternalServiceException ex = new ExternalServiceException("External Service Down");
        ResponseEntity<Map<String, Object>> resp = handler.handleExternalService(ex);
        assertBody(resp, HttpStatus.BAD_GATEWAY, "Bad Gateway",
                "External Service Down");
    }

    @Test
    @DisplayName("InvalidEventTypeException -> 400 BAD_REQUEST")
    void handleInvalidEventType() {
        InvalidEventTypeException ex = new InvalidEventTypeException("foobar");
        ResponseEntity<Map<String, Object>> resp = handler.handleInvalidEventType(ex);
        assertBody(resp, HttpStatus.BAD_REQUEST, "Bad Request",
                "Unknown or invalid event name: foobar");
    }

    @Test
    @DisplayName("InvalidPullRequestActionException -> 400 BAD_REQUEST")
    void handleInvalidPullRequestAction() {
        InvalidPullRequestActionException ex = new InvalidPullRequestActionException("approve");
        ResponseEntity<Map<String, Object>> resp = handler.handleInvalidPullRequestAction(ex);
        assertBody(resp, HttpStatus.BAD_REQUEST, "Bad Request",
                "Unknown or invalid pull request action: approve");
    }

    @Test
    @DisplayName("Exception genérica -> 500 INTERNAL_SERVER_ERROR")
    void handleUnexpected() {
        Exception ex = new Exception("Unexpected boom");
        ResponseEntity<Map<String, Object>> resp = handler.handleUnexpected(ex);
        assertBody(resp, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Unexpected boom");
    }
}
