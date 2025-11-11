package edu.itba.useractivity.domain.models;

import java.util.List;

public record CommitsResponse(
        List<Commit> commits,
        List<UserParticipation> userParticipations
) {}

