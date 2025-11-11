package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class GitHubClientException extends GitHubApiException {
    public GitHubClientException(int statusCode, String apiMessage) {
        super(statusCode, apiMessage);
    }
}

