package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class GitHubApiException extends RuntimeException {
    private final int statusCode;
    private final String apiMessage;

    public GitHubApiException(int statusCode, String apiMessage) {
        super("GitHub API error: " + apiMessage + " (status " + statusCode + ")");
        this.statusCode = statusCode;
        this.apiMessage = apiMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getApiMessage() {
        return apiMessage;
    }
}

