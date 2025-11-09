package edu.itba.useractivity.domain.models;

import lombok.Getter;

@Getter
public enum PullRequestAction {
    OPENED("opened"),
    CLOSED("closed"),
    REOPENED("reopened"),
    EDITED("edited"),
    ASSIGNED("assigned"),
    UNASSIGNED("unassigned"),
    LABELED("labeled"),
    UNLABELED("unlabeled"),
    SYNCHRONIZE("synchronize"),
    READY_FOR_REVIEW("ready_for_review"),
    CONVERTED_TO_DRAFT("converted_to_draft"),
    LOCKED("locked"),
    UNLOCKED("unlocked"),
    REVIEW_REQUESTED("review_requested"),
    REVIEW_REQUEST_REMOVED("review_request_removed"),
    MERGED("merged");

    private final String value;

    PullRequestAction(String value) {
        this.value = value;
    }

    public static PullRequestAction fromString(String action) {
        for (PullRequestAction a : values()) {
            if (a.value.equalsIgnoreCase(action)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unknown PullRequestAction: " + action);
    }
}

