package edu.itba.useractivity.infrastructure.adapters.driven.github.exceptions;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}