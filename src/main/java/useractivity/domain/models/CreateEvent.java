package useractivity.domain.models;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class CreateEvent extends Event {
    private final String ref;
    private final String refType;
    private final String masterBranch;
    private final String description;

    public CreateEvent(
            String id,
            User actor,
            Repository repo,
            ZonedDateTime createdAt,
            String ref,
            String refType,
            String masterBranch,
            String description
    ) {
        super(id, EventType.CREATE, actor, repo, createdAt);
        this.ref = ref;
        this.refType = refType;
        this.masterBranch = masterBranch;
        this.description = description;
    }
}
