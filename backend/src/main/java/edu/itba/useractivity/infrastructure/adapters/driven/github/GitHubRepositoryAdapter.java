package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryOutboundPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubClientException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubServerException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.ResourceNotFoundException;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubApiErrorCode; // <-- Importado
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional; // <-- Importado

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

    private void handleGitHubError(int statusCode, String resource, String resourceType) {
        if (statusCode == 200) {
            return;
        }

        Optional<GitHubApiErrorCode> errorCode = GitHubApiErrorCode.fromStatusCode(statusCode);

        if (errorCode.isPresent()) {
            switch (errorCode.get()) {
                case NOT_FOUND ->
                        throw new ResourceNotFoundException(resourceType + " not found for " + resource);
                case FORBIDDEN, UNAUTHORIZED, BAD_REQUEST, UNPROCESSABLE_ENTITY ->
                        throw new GitHubClientException(statusCode, "Client error when fetching " + resourceType.toLowerCase() + " for " + resource);
            }
        } else if (GitHubApiErrorCode.isServerError(statusCode)) {
            throw new GitHubServerException(statusCode, "GitHub server error while fetching " + resourceType.toLowerCase() + " for " + resource);
        }

        throw new GitHubClientException(statusCode, "Unhandled status code when fetching " + resourceType.toLowerCase() + " for " + resource);
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

            handleGitHubError(statusCode, resource, "Pull requests");

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

            handleGitHubError(statusCode, resource, "Commits");

            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapper.mapToCommits(rootNode);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch pullRequests from GitHub", e);
        }
    }
}
