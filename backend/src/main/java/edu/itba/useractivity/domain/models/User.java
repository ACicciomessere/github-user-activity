package edu.itba.useractivity.domain.models;

public record User (
    Long id,
    String username,
    String avatarUrl,
    String profileUrl,
    String type
){}
