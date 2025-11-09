package org.example.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ForkEvent extends Event {
    private final Repository forkee;

    public ForkEvent(String id, User actor, Repository repo, ZonedDateTime createdAt, Repository forkee) {
        super(id, EventType.FORK, actor, repo, createdAt);
        this.forkee = forkee;
    }
}
