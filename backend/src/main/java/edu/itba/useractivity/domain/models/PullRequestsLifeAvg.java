package edu.itba.useractivity.domain.models;

import java.time.Duration;

public record PullRequestsLifeAvg(String month, Duration hours, long count) {}
