package useractivity.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class Event {
    private String id;
    private EventType type;
    private User actor;
    private Repository repo;
    private ZonedDateTime createdAt;
}