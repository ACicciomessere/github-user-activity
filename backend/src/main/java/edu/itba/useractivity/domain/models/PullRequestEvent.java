package edu.itba.useractivity.domain.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PullRequestEvent extends Event {
    private final PullRequestAction action;
    private final PullRequest pullRequest;
}

