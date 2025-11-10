package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class ResourceNotFoundException extends GitHubApiException {
    public ResourceNotFoundException(String apiMessage) {
        super(404, apiMessage);
    }
}

