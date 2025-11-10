package edu.itba.useractivity.domain.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@SuperBuilder
public class Event {
    private String id;
    private EventType type;
    private User user;
    private Repository repo;
    private ZonedDateTime createdAt;
}