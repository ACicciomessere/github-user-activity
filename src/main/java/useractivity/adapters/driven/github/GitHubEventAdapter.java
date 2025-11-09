package useractivity.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import useractivity.domain.models.Event;
import useractivity.domain.ports.EventDataPort;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GitHubEventAdapter implements EventDataPort {

    private final GitHubEventMapper mapper;
    private final ObjectMapper objectMapper;

    public GitHubEventAdapter() {
        this.mapper = new GitHubEventMapper();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Event> getEventsByUser(String username) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/users/" + username + "/events"))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("GitHub API returned status " + response.statusCode());
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToEvents(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch events from GitHub", e);
        }
    }
}

