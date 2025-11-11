package edu.itba.useractivity.domain.enums;

import edu.itba.useractivity.domain.exceptions.InvalidEventTypeException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum EventType {
    PUSH("PushEvent"),
    PULL_REQUEST("PullRequestEvent"),
    FORK("ForkEvent"),
    CREATE("CreateEvent");

    private final String eventName;

    EventType(String eventName) {
        this.eventName = eventName;
    }

    public static EventType fromEventName(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            throw new InvalidEventTypeException(eventName);
        }
        return Arrays.stream(values())
                .filter(type -> type.eventName.equalsIgnoreCase(eventName))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidEventTypeException(eventName));

    }
}

