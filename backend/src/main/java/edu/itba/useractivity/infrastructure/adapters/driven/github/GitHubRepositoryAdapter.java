package edu.itba.useractivity.infrastructure.adapters.driven.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.itba.useractivity.domain.models.Commit;
import edu.itba.useractivity.domain.models.PullRequest;
import edu.itba.useractivity.domain.ports.outbound.RepositoryDataPort;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class GitHubRepositoryAdapter implements RepositoryDataPort {

    private static final String BASE_URL = "https://api.github.com/repos/";
    private final GitHubMapper mapper;
    private final ObjectMapper objectMapper;
    private final HttpClient client;

    public GitHubRepositoryAdapter() {
        this.mapper = new GitHubMapper();
        this.objectMapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public List<PullRequest> getPullRequests(String ownerName, String repositoryName) {
        String url = BASE_URL + ownerName + "/" + repositoryName + "/pulls?state=all";
        return fetchAndMap(url, "pull requests", mapper::mapToPullRequests);
    }

    @Override
    public List<Commit> getCommits(String ownerName, String repositoryName) {
        String url = BASE_URL + ownerName + "/" + repositoryName + "/commits";
        return fetchAndMap(url, "commits", mapper::mapToCommits);
    }

    private <T> List<T> fetchAndMap(String url, String resourceName, MapperFunction<JsonNode, List<T>> mapperFunction) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/vnd.github+json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 404) {
                throw new ResourceNotFoundException(resourceName + " not found for request: " + url);
            } else if (statusCode >= 400 && statusCode < 500) {
                throw new GitHubClientException(statusCode, "Client error when fetching " + resourceName);
            } else if (statusCode >= 500) {
                throw new GitHubServerException(statusCode, "GitHub server error while fetching " + resourceName);
            }

            JsonNode rootNode = objectMapper.readTree(response.body());
            return mapperFunction.apply(rootNode);

        } catch (IOException e) {
            throw new GitHubApiException(500, "Error parsing GitHub response: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GitHubApiException(500, "Request to GitHub API was interrupted");
        }
    }

    @FunctionalInterface
    private interface MapperFunction<T, R> {
        R apply(T t) throws IOException;
    }
}

