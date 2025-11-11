package edu.itba.useractivity.domain.exceptions;

public class InvalidPullRequestActionException extends RuntimeException {
    public InvalidPullRequestActionException(String action) {
        super("Unknown or invalid pull request action:" + action);
    }
}
