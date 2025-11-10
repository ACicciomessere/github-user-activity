package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;
import edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions.GitHubApiException;

public class GitHubClientException extends GitHubApiException {
    public GitHubClientException(int statusCode, String apiMessage) {
        super(statusCode, apiMessage);
    }
}

