package edu.itba.useractivity.domain.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateEvent extends Event {
    private String ref;
    private String refType;
    private String masterBranch;
    private String description;
}
