package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class GitHubServerException extends GitHubApiException {
    public GitHubServerException(int statusCode, String apiMessage) {
        super(statusCode, apiMessage);
    }
}
