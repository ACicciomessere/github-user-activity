package useractivity.domain.models;

import lombok.Getter;

@Getter
public enum EventType {
    PUSH("PushEvent"),
    PULL_REQUEST("PullRequestEvent"),
    FORK("ForkEvent"),
    CREATE("CreateEvent");

    private final String apiName;

    EventType(String apiName) {
        this.apiName = apiName;
    }

    public static EventType fromApiName(String apiName) {
        for (EventType type : values()) {
            if (type.apiName.equalsIgnoreCase(apiName)) {
                return type;
            }
        }
        return null;
    }
}

