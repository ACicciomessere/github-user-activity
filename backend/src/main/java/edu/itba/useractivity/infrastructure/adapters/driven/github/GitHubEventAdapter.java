package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventDataPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional; // <-- Importado

@Component
public class GitHubEventAdapter implements EventDataPort {

    private final GitHubMapper mapper;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public GitHubEventAdapter() {
        this.mapper = new GitHubMapper();
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public List<Event> getEventsByUser(String username, int page, int perPage) {
        try {
            String url = "https://api.github.com/users/" + username + "/events?page=" + page + "&per_page=" + perPage;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();

            if (status == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                return mapper.mapToEvents(rootNode);
            }

            Optional<GitHubApiErrorCode> errorCode = GitHubApiErrorCode.fromStatusCode(status);

            if (errorCode.isPresent()) {
                switch (errorCode.get()) {
                    case NOT_FOUND -> throw new ResourceNotFoundException("User '" + username + "' not found on GitHub");
                    case FORBIDDEN -> throw new RateLimitExceededException("GitHub API rate limit exceeded (status " + status + ")");
                    case UNAUTHORIZED, BAD_REQUEST, UNPROCESSABLE_ENTITY ->
                            throw new GitHubClientException(status, "GitHub API client error: " + errorCode.get());
                }
            }

            if (GitHubApiErrorCode.isServerError(status)) {
                throw new GitHubServerException(status, "GitHub API server error: " + status);
            }

            throw new GitHubApiException(status, "Unhandled GitHub API status: " + status);

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("Failed to fetch events from GitHub", e);
        }
    }
}


