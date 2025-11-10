package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class GitHubRepositoryAdapter implements RepositoryDataPort {

    private final GitHubMapper mapper;
    private final ObjectMapper objectMapper;

    public GitHubRepositoryAdapter() {
        this.mapper = new GitHubMapper();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<PullRequest> getPullRequests(String ownerName, String repositoryName) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + ownerName + "/" + repositoryName + "/pulls?state=all"))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // TODO manejo de errores
                throw new RuntimeException("GitHub API returned status " + response.statusCode());
            }
            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToPullRequests(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch pullRequests from GitHub", e);
        }
    }

    @Override
    public List<Commit> getCommits(String ownerName, String repositoryName) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + ownerName + "/" + repositoryName + "/commits"))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // TODO manejo de errores
                throw new RuntimeException("GitHub API returned status " + response.statusCode());
            }
            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToCommits(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch pullRequests from GitHub", e);
        }
    }
}
