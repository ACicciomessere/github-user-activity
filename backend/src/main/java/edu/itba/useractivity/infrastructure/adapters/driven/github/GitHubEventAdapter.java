package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Event;
import edu.itba.useractivity.domain.ports.outbound.EventDataPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ExternalServiceException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.RateLimitExceededException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

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

            switch (status) {
                case 200 -> {
                    JsonNode rootNode = objectMapper.readTree(response.body());
                    return mapper.mapToEvents(rootNode);
                }
                case 404 -> throw new ResourceNotFoundException("User '" + username + "' not found on GitHub");
                case 403 -> throw new RateLimitExceededException("GitHub API rate limit exceeded");
                default -> throw new ExternalServiceException("GitHub API returned unexpected status: " + status);
            }

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalServiceException("Failed to fetch events from GitHub", e);
        }
    }
}


