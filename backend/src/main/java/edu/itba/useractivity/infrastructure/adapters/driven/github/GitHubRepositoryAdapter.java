package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubServerException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class GitHubRepositoryAdapter implements RepositoryOutboundPort {

    private final GitHubMapper mapper;
    private final ObjectMapper objectMapper;
    private final HttpClient client;

    public GitHubRepositoryAdapter() {
        this.mapper = new GitHubMapper();
        this.objectMapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public List<PullRequest> getPullRequests(String ownerName, String repositoryName, int page, int perPage) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + ownerName + "/" + repositoryName + "/pulls?state=all&page=" + page + "&per_page=" + perPage))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String resource = String.format("%s/%s", ownerName, repositoryName);
            if (statusCode == 404) {
                throw new ResourceNotFoundException("Pull requests not found for " + resource);
            } else if (statusCode >= 400 && statusCode < 500) {
                throw new GitHubClientException(statusCode, "Client error when fetching pull requests for " + resource);
            } else if (statusCode >= 500) {
                throw new GitHubServerException(statusCode, "GitHub server error while fetching pull requests for " + resource);
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToPullRequests(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch pullRequests from GitHub", e);
        }
    }

    @Override
    public List<Commit> getCommits(String ownerName, String repositoryName, int page, int perPage) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + ownerName + "/" + repositoryName + "/commits?page=" + page + "&per_page=" + perPage))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String resource = String.format("%s/%s", ownerName, repositoryName);
            if (statusCode == 404) {
                throw new ResourceNotFoundException("Commits not found for " + resource);
            } else if (statusCode >= 400 && statusCode < 500) {
                throw new GitHubClientException(statusCode, "Client error when fetching commits for" + resource);
            } else if (statusCode >= 500) {
                throw new GitHubServerException(statusCode, "GitHub server error while fetching commits for" + resource);
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToCommits(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch pullRequests from GitHub", e);
        }
    }
}
