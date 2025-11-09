package edu.itba.useractivity.domain.models;

import lombok.Getter;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class PushEvent extends Event {
    private final String ref;
    private final String before;
    private final String head;
    private final List<Commit> commits;

    public PushEvent(
            String id,
            User actor,
            Repository repo,
            ZonedDateTime createdAt,
            String ref,
            String before,
            String head,
            List<Commit> commits
    ) {
        super(id, EventType.PUSH, actor, repo, createdAt);
        this.ref = ref;
        this.before = before;
        this.head = head;
        this.commits = commits;
    }
}
