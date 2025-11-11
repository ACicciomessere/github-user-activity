package edu.itba.useractivity.domain.models;

public record UserParticipation(
        String username,
        long commitCount,
        double percentage
) {}

