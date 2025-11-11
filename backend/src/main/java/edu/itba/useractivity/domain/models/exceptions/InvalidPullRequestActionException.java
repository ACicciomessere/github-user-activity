package edu.itba.useractivity.domain.models.exceptions;

public class InvalidPullRequestActionException extends RuntimeException {
    public InvalidPullRequestActionException(String action) {
        super("Unknown or invalid pull request action: " + action);
    }
}
