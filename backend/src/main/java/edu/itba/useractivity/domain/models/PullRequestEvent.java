package edu.itba.useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class PullRequestEvent extends Event {
    private final PullRequestAction action;
    private final PullRequest pullRequest;

    public PullRequestEvent(
            String id,
            User actor,
            Repository repo,
            ZonedDateTime createdAt,
            PullRequestAction action,
            PullRequest pullRequest
    ) {
        super(id, EventType.PULL_REQUEST, actor, repo, createdAt);
        this.action = action;
        this.pullRequest = pullRequest;
    }
}

