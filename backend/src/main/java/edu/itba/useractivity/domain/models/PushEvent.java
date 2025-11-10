package edu.itba.useractivity.domain.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class PushEvent extends Event {
    private final String ref;
    private final String before;
    private final String head;
    private final List<Commit> commits;
}
