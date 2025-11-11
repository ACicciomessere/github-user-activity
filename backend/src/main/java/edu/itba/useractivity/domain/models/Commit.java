package edu.itba.useractivity.domain.models;

import java.time.ZonedDateTime;

public record Commit(
        String sha,
        String message,
        String authorName,
        ZonedDateTime date,
        String htmlUrl
) {}