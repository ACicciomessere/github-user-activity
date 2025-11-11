package edu.itba.useractivity.domain.models;

import edu.itba.useractivity.domain.enums.PullRequestAction;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PullRequestEvent extends Event {
    private final PullRequestAction action;
    private final PullRequest pullRequest;
}

