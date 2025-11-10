package edu.itba.useractivity.domain.models;

import lombok.Getter;

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
        for (EventType type : values()) {
            if (type.eventName.equalsIgnoreCase(eventName)) {
                return type;
            }
        }
        return null;
    }
}

