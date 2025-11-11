package edu.itba.useractivity.domain.models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ForkEvent extends Event {
    private final Repository forkee;
}
